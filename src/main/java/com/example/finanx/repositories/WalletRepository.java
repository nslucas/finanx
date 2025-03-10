package com.example.finanx.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.finanx.Entities.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
}
