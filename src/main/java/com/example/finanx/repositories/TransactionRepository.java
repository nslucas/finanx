package com.example.finanx.repositories;

import com.example.finanx.Entities.Transaction;
import com.example.finanx.Entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByIdAndUserId(Integer id, Integer userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
            "AND (:accountId IS NULL OR t.accountId = :accountId) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:month IS NULL OR MONTH(t.occurredAt) = :month) " +
            "AND (:year IS NULL OR YEAR(t.occurredAt) = :year) " +
            "ORDER BY t.occurredAt DESC, t.id DESC")
    List<Transaction> findByFilters(@Param("userId") Integer userId, @Param("month") Integer month,
                                    @Param("year") Integer year, @Param("accountId") Integer accountId,
                                    @Param("type") TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.type = :type " +
            "AND MONTH(t.occurredAt) = :month AND YEAR(t.occurredAt) = :year")
    BigDecimal sumByUserIdTypeAndMonth(@Param("userId") Integer userId, @Param("type") TransactionType type,
                                       @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT t.categoryId, SUM(t.amount) FROM Transaction t WHERE t.userId = :userId " +
            "AND t.type = :type " +
            "AND MONTH(t.occurredAt) = :month AND YEAR(t.occurredAt) = :year " +
            "GROUP BY t.categoryId")
    List<Object[]> sumExpenseTransactionsByCategory(@Param("userId") Integer userId,
                                                    @Param("month") Integer month,
                                                    @Param("year") Integer year,
                                                    @Param("type") TransactionType type);
}
