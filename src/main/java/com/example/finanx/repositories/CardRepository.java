package com.example.finanx.repositories;

import com.example.finanx.entities.Card;
import com.example.finanx.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, String> {
}
