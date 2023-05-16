package com.example.finanx.repositories;

import com.example.finanx.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<User, Long> {
    BigDecimal sumExpensesByUserAndMonth(BigDecimal expenseValue, LocalDate now);
}
