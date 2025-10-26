package com.swisspost.swisscrypto.service;

import com.swisspost.swisscrypto.entity.Asset;
import com.swisspost.swisscrypto.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class PriceUpdateService {

    private static final Logger log = LoggerFactory.getLogger(PriceUpdateService.class);

    private final AssetRepository assetRepository;
    private final CoinCapService coinCapService;
    private final Executor priceUpdateExecutor;

    public PriceUpdateService(
        AssetRepository assetRepository,
        CoinCapService coinCapService,
        @Qualifier("priceUpdateExecutor") Executor priceUpdateExecutor
    ) {
        this.assetRepository = assetRepository;
        this.coinCapService = coinCapService;
        this.priceUpdateExecutor = priceUpdateExecutor;
    }

    public void updateAllAssetPrices() {
        List<String> symbols = assetRepository.findDistinctSymbols();

        if (symbols.isEmpty()) {
            log.debug("No assets to update");
            return;
        }

        log.info("Starting price update for {} symbols", symbols.size());

        List<CompletableFuture<Void>> futures = symbols.stream()
            .map(symbol -> CompletableFuture.runAsync(
                () -> updatePriceForSymbol(symbol),
                priceUpdateExecutor
            ))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Price update completed for all symbols");
    }

    @Transactional
    protected void updatePriceForSymbol(String symbol) {
        log.debug("Fetching price for symbol: {}", symbol);

        BigDecimal currentPrice = coinCapService.getCurrentPrice(symbol);

        List<Asset> assets = assetRepository.findBySymbol(symbol);
        assets.forEach(asset -> asset.setPrice(currentPrice));
        assetRepository.saveAll(assets);

        log.debug("Updated {} assets for symbol {} with price {}",
                 assets.size(), symbol, currentPrice);
    }
}
