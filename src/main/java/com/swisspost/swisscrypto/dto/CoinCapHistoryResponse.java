package com.swisspost.swisscrypto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoinCapHistoryResponse(
    List<HistoryData> data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HistoryData(
        BigDecimal priceUsd,
        long time
    ) {}
}
