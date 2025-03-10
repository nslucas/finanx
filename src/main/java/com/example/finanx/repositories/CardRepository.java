package com.example.finanx.Repositories;

import com.example.finanx.Entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, String> {
}
