package com.swisspost.swisscrypto.service;

import com.swisspost.swisscrypto.dto.AddAssetRequest;
import com.swisspost.swisscrypto.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Transactional
class WalletServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private WalletService walletService;

    @Test
    void shouldCreateWallet() {
        WalletResponse response = walletService.createWallet("test@example.com");

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.total()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.assets()).isEmpty();
    }

    @Test
    void shouldThrowExceptionForDuplicateEmail() {
        walletService.createWallet("duplicate@example.com");

        assertThatThrownBy(() -> walletService.createWallet("duplicate@example.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void shouldGetWallet() {
        WalletResponse created = walletService.createWallet("fetch@example.com");

        WalletResponse fetched = walletService.getWallet(created.id());

        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.email()).isEqualTo("fetch@example.com");
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> walletService.getWallet(nonExistentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void shouldAddAsset() {
        WalletResponse wallet = walletService.createWallet("asset@example.com");
        AddAssetRequest assetRequest = new AddAssetRequest("BTC", new BigDecimal("1.5"), new BigDecimal("50000.00"));

        WalletResponse updated = walletService.addAssetToWallet(wallet.id(), assetRequest);

        assertThat(updated.assets()).hasSize(1);
        assertThat(updated.assets().getFirst().symbol()).isEqualTo("BTC");
        assertThat(updated.assets().getFirst().quantity()).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void shouldCalculateTotalCorrectly() {
        WalletResponse wallet = walletService.createWallet("multi@example.com");

        walletService.addAssetToWallet(wallet.id(),
            new AddAssetRequest("BTC", new BigDecimal("1.0"), new BigDecimal("50000.00")));
        WalletResponse updated = walletService.addAssetToWallet(wallet.id(),
            new AddAssetRequest("ETH", new BigDecimal("10.0"), new BigDecimal("3000.00")));

        assertThat(updated.assets()).hasSize(2);
        assertThat(updated.total()).isEqualByComparingTo(new BigDecimal("80000.00"));
    }

    @Test
    void shouldRejectInvalidSymbol() {
        WalletResponse wallet = walletService.createWallet("invalid@example.com");
        AddAssetRequest invalidAsset = new AddAssetRequest("INVALID", new BigDecimal("1.0"), new BigDecimal("100.00"));

        assertThatThrownBy(() -> walletService.addAssetToWallet(wallet.id(), invalidAsset))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid cryptocurrency symbol");
    }

}
