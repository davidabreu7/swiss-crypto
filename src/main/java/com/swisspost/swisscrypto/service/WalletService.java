package com.swisspost.swisscrypto.service;

import com.swisspost.swisscrypto.dto.AddAssetRequest;
import com.swisspost.swisscrypto.dto.AssetResponse;
import com.swisspost.swisscrypto.dto.WalletResponse;
import com.swisspost.swisscrypto.entity.Asset;
import com.swisspost.swisscrypto.entity.User;
import com.swisspost.swisscrypto.entity.Wallet;
import com.swisspost.swisscrypto.repository.AssetRepository;
import com.swisspost.swisscrypto.repository.UserRepository;
import com.swisspost.swisscrypto.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final CoinCapService coinCapService;

    public WalletService(
        UserRepository userRepository,
        WalletRepository walletRepository,
        AssetRepository assetRepository,
        CoinCapService coinCapService
    ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.assetRepository = assetRepository;
        this.coinCapService = coinCapService;
    }

    @Transactional
    public WalletResponse createWallet(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = User.create(email);
        Wallet wallet = Wallet.create(user);
        user.setWallet(wallet);

        User savedUser = userRepository.save(user);

        log.info("Created wallet for user: {}", email);
        return toWalletResponse(savedUser.getWallet());
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        return toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse addAssetToWallet(UUID walletId, AddAssetRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        if (!coinCapService.isValidSymbol(request.symbol())) {
            throw new IllegalArgumentException("Invalid cryptocurrency symbol: " + request.symbol());
        }

        BigDecimal currentPrice = coinCapService.getCurrentPrice(request.symbol());
        log.debug("Current price for {}: {}", request.symbol(), currentPrice);

        Asset asset = Asset.create(request.symbol(), request.quantity(), request.price());
        wallet.addAsset(asset);

        walletRepository.save(wallet);

        log.info("Added asset {} to wallet {}", request.symbol(), walletId);
        return toWalletResponse(wallet);
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        List<AssetResponse> assetResponses = wallet.getAssets().stream()
            .map(asset -> new AssetResponse(
                asset.getId(),
                asset.getSymbol(),
                asset.getQuantity(),
                asset.getPrice(),
                asset.getValue()
            ))
            .toList();

        return new WalletResponse(
            wallet.getId(),
            wallet.getUser().getEmail(),
            wallet.getTotalValue(),
            assetResponses
        );
    }
}
