package com.swisspost.swisscrypto.dto;

import java.math.BigDecimal;

public record SimulationResponse(
    BigDecimal total,
    String bestAsset,
    BigDecimal bestPerformance,
    String worstAsset,
    BigDecimal worstPerformance
) {}
