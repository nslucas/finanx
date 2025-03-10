package com.example.finanx.Repositories;

import com.example.finanx.Entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.userId = :userId")
    Double sumAmountByUserId(@Param("userId") Integer userId);
}

