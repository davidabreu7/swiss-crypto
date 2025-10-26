package com.swisspost.swisscrypto.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "coincap.api")
@Validated
public class CoinCapProperties {

    @NotBlank
    private String key;

    @NotBlank
    private String baseUrl = "https://rest.coincap.io/v3";

    private int timeout = 5000;

    public String getKey() { return key; }
    public String getBaseUrl() { return baseUrl; }
    public int getTimeout() { return timeout; }

    public void setKey(String key) { this.key = key; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
}
