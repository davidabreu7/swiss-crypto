package com.swisspost.swisscrypto.repository;

import com.swisspost.swisscrypto.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

    @Query("""
        SELECT DISTINCT a.symbol
        FROM Asset a
        ORDER BY a.symbol
        """)
    List<String> findDistinctSymbols();

    List<Asset> findBySymbol(String symbol);
}
