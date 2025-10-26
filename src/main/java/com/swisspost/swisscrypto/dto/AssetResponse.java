package com.swisspost.swisscrypto.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AssetResponse(
    UUID id,
    String symbol,
    BigDecimal quantity,
    BigDecimal price,
    BigDecimal value
) {
}
