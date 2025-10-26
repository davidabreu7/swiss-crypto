package com.swisspost.swisscrypto.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wallet wallet;

    protected User() {}

    private User(String email) {
        this.email = email;
    }

    // Static factory method
    public static User create(String email) {
        return new User(email);
    }

    // Standard JavaBeans getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public Wallet getWallet() { return wallet; }

    // Standard JavaBeans setters
    public void setEmail(String email) { this.email = email; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User other && Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User[id=%s, email=%s]".formatted(id, email);
    }
}
