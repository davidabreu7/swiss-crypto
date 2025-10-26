package com.swisspost.swisscrypto.controller;

import com.swisspost.swisscrypto.dto.AddAssetRequest;
import com.swisspost.swisscrypto.dto.CreateWalletRequest;
import com.swisspost.swisscrypto.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class WalletControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnCreatedWhenCreatingWallet() {
        CreateWalletRequest request = new CreateWalletRequest("api@example.com");

        ResponseEntity<WalletResponse> response = restTemplate.postForEntity(
            "/api/wallets",
            request,
            WalletResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("api@example.com");
    }

    @Test
    void shouldReturnOkWhenGettingWallet() {
        CreateWalletRequest createRequest = new CreateWalletRequest("fetch@example.com");
        ResponseEntity<WalletResponse> createResponse = restTemplate.postForEntity(
            "/api/wallets",
            createRequest,
            WalletResponse.class
        );
        UUID walletId = createResponse.getBody().id();

        ResponseEntity<WalletResponse> getResponse = restTemplate.getForEntity(
            "/api/wallets/" + walletId,
            WalletResponse.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().id()).isEqualTo(walletId);
    }

    @Test
    void shouldReturnBadRequestForNonExistentWallet() {
        UUID nonExistentId = UUID.randomUUID();

        ResponseEntity<Map> response = restTemplate.getForEntity(
            "/api/wallets/" + nonExistentId,
            Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnOkWhenAddingAsset() {
        CreateWalletRequest createRequest = new CreateWalletRequest("asset@example.com");
        ResponseEntity<WalletResponse> createResponse = restTemplate.postForEntity(
            "/api/wallets",
            createRequest,
            WalletResponse.class
        );
        UUID walletId = createResponse.getBody().id();

        AddAssetRequest assetRequest = new AddAssetRequest("BTC", new BigDecimal("1.0"), new BigDecimal("50000.00"));

        ResponseEntity<WalletResponse> addAssetResponse = restTemplate.postForEntity(
            "/api/wallets/" + walletId + "/assets",
            assetRequest,
            WalletResponse.class
        );

        assertThat(addAssetResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addAssetResponse.getBody().assets()).hasSize(1);
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() {
        CreateWalletRequest createRequest = new CreateWalletRequest("validation@example.com");
        ResponseEntity<WalletResponse> createResponse = restTemplate.postForEntity(
            "/api/wallets",
            createRequest,
            WalletResponse.class
        );
        UUID walletId = createResponse.getBody().id();

        AddAssetRequest invalidAsset = new AddAssetRequest("INVALID", new BigDecimal("1.0"), new BigDecimal("100.00"));

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/wallets/" + walletId + "/assets",
            invalidAsset,
            Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
    }
}
