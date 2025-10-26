package com.swisspost.swisscrypto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddAssetRequest(
    @NotBlank(message = "Symbol is required")
    String symbol,

    @Positive(message = "Quantity must be positive")
    BigDecimal quantity,

    @Positive(message = "Price must be positive")
    BigDecimal price
) {
}
