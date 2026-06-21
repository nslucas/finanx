package com.example.prospera.repositories;

import com.example.prospera.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByUserIdAndActiveTrueOrderByNameAsc(Integer userId);

    Optional<Account> findByIdAndUserId(Integer id, Integer userId);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.userId = :userId AND a.active = true")
    BigDecimal sumActiveBalancesByUserId(@Param("userId") Integer userId);
}
