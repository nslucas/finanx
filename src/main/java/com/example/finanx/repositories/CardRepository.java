package com.example.finanx.repositories;

import com.example.finanx.Entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(Integer userId);

    Optional<Card> findByIdAndUserId(Integer id, Integer userId);
}
