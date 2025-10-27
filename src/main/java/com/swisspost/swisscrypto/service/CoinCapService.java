package com.swisspost.swisscrypto.service;

import com.swisspost.swisscrypto.config.CoinCapProperties;
import com.swisspost.swisscrypto.dto.CoinCapHistoryResponse;
import com.swisspost.swisscrypto.dto.CoinCapResponse;
import com.swisspost.swisscrypto.model.CryptoSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class CoinCapService {

    private static final Logger log = LoggerFactory.getLogger(CoinCapService.class);

    private final RestClient restClient;

    public CoinCapService(CoinCapProperties properties) {
        this.restClient = RestClient.builder()
            .baseUrl(properties.getBaseUrl())
            .defaultHeader("Authorization", "Bearer " + properties.getKey())
            .build();
    }

    public boolean isValidSymbol(String symbol) {
        return CryptoSymbol.isValid(symbol);
    }

    public BigDecimal getCurrentPrice(String symbol) {
        CryptoSymbol crypto = CryptoSymbol.fromSymbol(symbol)
            .orElseThrow(() -> new IllegalArgumentException("Invalid or unsupported symbol: " + symbol));

        CoinCapResponse response = restClient.get()
            .uri("/assets/" + crypto.getCoinCapId())
            .retrieve()
            .body(CoinCapResponse.class);

        if (response == null || response.data() == null) {
            throw new IllegalStateException("Empty response from CoinCap API for symbol: " + symbol);
        }

        BigDecimal price = response.data().priceUsd();
        log.debug("Fetched price for {}: {}", symbol, price);

        return price;
    }

    public BigDecimal getHistoricalPrice(String symbol, LocalDate date) {
        CryptoSymbol crypto = CryptoSymbol.fromSymbol(symbol)
            .orElseThrow(() -> new IllegalArgumentException("Invalid or unsupported symbol: " + symbol));

        long startTimestamp = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endTimestamp = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        CoinCapHistoryResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/assets/" + crypto.getCoinCapId() + "/history")
                .queryParam("interval", "d1")
                .queryParam("start", startTimestamp)
                .queryParam("end", endTimestamp)
                .build())
            .retrieve()
            .body(CoinCapHistoryResponse.class);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            throw new IllegalStateException("No historical price data available for " + symbol + " on " + date);
        }

        BigDecimal price = response.data().get(0).priceUsd();
        log.debug("Fetched historical price for {} on {}: {}", symbol, date, price);

        return price;
    }
}
