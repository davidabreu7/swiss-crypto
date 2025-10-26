package com.swisspost.swisscrypto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assets", indexes = {
    @Index(name = "idx_asset_symbol", columnList = "symbol"),
    @Index(name = "idx_asset_wallet", columnList = "wallet_id")
})
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal price;

    protected Asset() {}

    private Asset(String symbol, BigDecimal quantity, BigDecimal price) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    // Static factory method
    public static Asset create(String symbol, BigDecimal quantity, BigDecimal price) {
        return new Asset(symbol, quantity, price);
    }

    // Standard JavaBeans getters
    public UUID getId() { return id; }
    public Wallet getWallet() { return wallet; }
    public String getSymbol() { return symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

    // Standard JavaBeans setters
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
    public void setPrice(BigDecimal price) { this.price = price; }

    // Business logic
    public BigDecimal getValue() {
        return quantity.multiply(price);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Asset other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
