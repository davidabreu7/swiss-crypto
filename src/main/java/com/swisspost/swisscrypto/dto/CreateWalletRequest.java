package com.swisspost.swisscrypto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateWalletRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email
) {
}
