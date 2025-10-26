package com.swisspost.swisscrypto.controller;

import com.swisspost.swisscrypto.dto.AddAssetRequest;
import com.swisspost.swisscrypto.dto.CreateWalletRequest;
import com.swisspost.swisscrypto.dto.WalletResponse;
import com.swisspost.swisscrypto.service.WalletService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        log.info("Creating wallet for email: {}", request.email());
        WalletResponse response = walletService.createWallet(request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID walletId) {
        log.debug("Retrieving wallet: {}", walletId);
        WalletResponse response = walletService.getWallet(walletId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{walletId}/assets")
    public ResponseEntity<WalletResponse> addAsset(
        @PathVariable UUID walletId,
        @Valid @RequestBody AddAssetRequest request
    ) {
        log.info("Adding asset {} to wallet {}", request.symbol(), walletId);
        WalletResponse response = walletService.addAssetToWallet(walletId, request);
        return ResponseEntity.ok(response);
    }
}
