package com.swisspost.swisscrypto.service;

import com.swisspost.swisscrypto.dto.SimulationAssetRequest;
import com.swisspost.swisscrypto.dto.SimulationRequest;
import com.swisspost.swisscrypto.dto.SimulationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class WalletSimulationService {

    private static final Logger log = LoggerFactory.getLogger(WalletSimulationService.class);

    private final CoinCapService coinCapService;

    public WalletSimulationService(CoinCapService coinCapService) {
        this.coinCapService = coinCapService;
    }

    public SimulationResponse simulate(SimulationRequest request) {
        Map<String, AssetPerformance> performances = new HashMap<>();
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (SimulationAssetRequest asset : request.assets()) {
            if (!coinCapService.isValidSymbol(asset.symbol())) {
                throw new IllegalArgumentException("Invalid cryptocurrency symbol: " + asset.symbol());
            }

            BigDecimal originalPricePerUnit = asset.value().divide(asset.quantity(), 10, RoundingMode.HALF_UP);
            BigDecimal currentPrice = coinCapService.getCurrentPrice(asset.symbol());
            BigDecimal currentValue = asset.quantity().multiply(currentPrice);

            BigDecimal performancePercentage = currentPrice
                .subtract(originalPricePerUnit)
                .divide(originalPricePerUnit, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

            performances.put(asset.symbol(), new AssetPerformance(performancePercentage, currentValue));
            totalCurrentValue = totalCurrentValue.add(currentValue);

            log.debug("Asset {}: original=${}, current=${}, performance={}%",
                asset.symbol(), originalPricePerUnit, currentPrice, performancePercentage);
        }

        String bestAsset = null;
        BigDecimal bestPerformance = null;
        String worstAsset = null;
        BigDecimal worstPerformance = null;

        for (Map.Entry<String, AssetPerformance> entry : performances.entrySet()) {
            BigDecimal performance = entry.getValue().performance;

            if (bestPerformance == null || performance.compareTo(bestPerformance) > 0) {
                bestAsset = entry.getKey();
                bestPerformance = performance;
            }

            if (worstPerformance == null || performance.compareTo(worstPerformance) < 0) {
                worstAsset = entry.getKey();
                worstPerformance = performance;
            }
        }

        log.info("Simulation complete: total=${}, best={} ({}%), worst={} ({}%)",
            totalCurrentValue, bestAsset, bestPerformance, worstAsset, worstPerformance);

        return new SimulationResponse(
            totalCurrentValue.setScale(2, RoundingMode.HALF_UP),
            bestAsset,
            bestPerformance.setScale(2, RoundingMode.HALF_UP),
            worstAsset,
            worstPerformance.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private record AssetPerformance(BigDecimal performance, BigDecimal currentValue) {}
}
