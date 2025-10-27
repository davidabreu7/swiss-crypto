package com.swisspost.swisscrypto.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SimulationRequest(
    @NotEmpty(message = "Assets list cannot be empty")
    @Valid
    List<SimulationAssetRequest> assets
) {}
