package com.swisspost.swisscrypto.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets = new ArrayList<>();

    protected Wallet() {}

    private Wallet(User user) {
        this.user = user;
    }

    // Static factory method
    public static Wallet create(User user) {
        return new Wallet(user);
    }

    // Standard JavaBeans getters
    public UUID getId() { return id; }
    public User getUser() { return user; }
    public List<Asset> getAssets() { return assets; }

    // Business logic methods
    public BigDecimal getTotalValue() {
        return assets.stream()
            .map(asset -> asset.getPrice().multiply(asset.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
        asset.setWallet(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Wallet other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
