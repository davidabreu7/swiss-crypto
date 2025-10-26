package com.swisspost.swisscrypto.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WalletResponse(
    UUID id,
    String email,
    BigDecimal total,
    List<AssetResponse> assets
) {
}
