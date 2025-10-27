package com.swisspost.swisscrypto.model;

import java.util.Arrays;
import java.util.Optional;

public enum CryptoSymbol {
    BTC("bitcoin"),
    ETH("ethereum"),
    USDT("tether"),
    USDC("usd-coin"),
    BNB("binance-coin"),
    XRP("xrp"),
    ADA("cardano"),
    SOL("solana"),
    DOT("polkadot"),
    DOGE("dogecoin");

    private final String coinCapId;

    CryptoSymbol(String coinCapId) {
        this.coinCapId = coinCapId;
    }

    public String getCoinCapId() {
        return coinCapId;
    }

    public static Optional<CryptoSymbol> fromSymbol(String symbol) {
        return Arrays.stream(values())
            .filter(crypto -> crypto.name().equalsIgnoreCase(symbol) ||
                             crypto.coinCapId.equalsIgnoreCase(symbol))
            .findFirst();
    }

    public static boolean isValid(String symbol) {
        return fromSymbol(symbol).isPresent();
    }
}
