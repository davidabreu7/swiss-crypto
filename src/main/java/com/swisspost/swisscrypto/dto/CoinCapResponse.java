package com.swisspost.swisscrypto.dto;

public record CoinCapResponse(
    CoinCapAssetData data,
    Long timestamp
) {
}
