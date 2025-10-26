package com.swisspost.swisscrypto.dto;

import java.math.BigDecimal;

public record CoinCapAssetData(
    String id,
    String rank,
    String symbol,
    String name,
    BigDecimal supply,
    BigDecimal maxSupply,
    BigDecimal marketCapUsd,
    BigDecimal volumeUsd24Hr,
    BigDecimal priceUsd,
    BigDecimal changePercent24Hr,
    BigDecimal vwap24Hr
) {
}
