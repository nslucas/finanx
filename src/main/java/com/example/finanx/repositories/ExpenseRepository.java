package com.example.finanx.Repositories;

import com.example.finanx.Entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId")
    BigDecimal sumAmountByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate) = MONTH(CURRENT_DATE) AND YEAR(e.purchaseDate) = YEAR(CURRENT_DATE)  ")
    BigDecimal sumAmountByUserIdInCurrentMonth(@Param("userId") Integer userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate)  = :month AND YEAR(e.purchaseDate) = :year  ")
    BigDecimal sumAmountByUserIdInAnyMonth(@Param("userId") Integer userid, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate) = MONTH(CURRENT_DATE) AND YEAR(e.purchaseDate) = YEAR(CURRENT_DATE)")
    List<Expense> findExpensesByUserIdAndPurchaseDateInCurrentMonth(@Param("userId") Integer userId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "and MONTH(e.purchaseDate) = :month AND YEAR(e.purchaseDate) = :year")
    List<Expense> findExpensesByUserIdAndPurchaseDateInAnyMonth(@Param("userId") Integer userid, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId " +
            "AND (:cardId IS NULL OR e.cardId = :cardId) " +
            "AND (:month IS NULL OR MONTH(e.purchaseDate) = :month) " +
            "AND (:year IS NULL OR YEAR(e.purchaseDate) = :year)")
    List<Expense> findByFilters(@Param("userId") Integer userId, @Param("month") Integer month,
                                @Param("year") Integer year, @Param("cardId") Integer cardId);
}
