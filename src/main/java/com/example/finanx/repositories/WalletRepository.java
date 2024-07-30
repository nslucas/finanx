package com.example.finanx.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.finanx.entities.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
