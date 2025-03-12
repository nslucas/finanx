package com.example.finanx.Repositories;

import com.example.finanx.Entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.userId = :userId")
    Double sumAmountByUserId(@Param("userId") Integer userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate) = MONTH(CURRENT_DATE) AND YEAR(e.purchaseDate) = YEAR(CURRENT_DATE)  ")
    Double sumAmountByUserIdInCurrentMonth(@Param("userId") Integer userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate)  = :month AND YEAR(e.purchaseDate) = :year  ")
    Double sumAmountByUserIdInAnyMonth(@Param("userId") Integer userid, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate) = MONTH(CURRENT_DATE) AND YEAR(e.purchaseDate) = YEAR(CURRENT_DATE)")
    List<Expense> findExpensesByUserIdAndPurchaseDateInCurrentMonth(@Param("userId") Integer userId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate) = :month AND YEAR(e.purchaseDate) = :year")
    List<Expense> findExpensesByUserIdAndPurchaseDateInAnyMonth(@Param("userId") Integer userid, @Param("month") Integer month, @Param("year") Integer year);
}

