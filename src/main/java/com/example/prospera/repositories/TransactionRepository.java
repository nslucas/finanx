package com.example.prospera.repositories;

import com.example.prospera.Entities.Transaction;
import com.example.prospera.Entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByIdAndUserId(Integer id, Integer userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
            "AND (:accountId IS NULL OR t.accountId = :accountId) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:from IS NULL OR t.occurredAt >= :from) " +
            "AND (:to IS NULL OR t.occurredAt < :to) " +
            "ORDER BY t.occurredAt DESC, t.id DESC")
    List<Transaction> findByFilters(@Param("userId") Integer userId, @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to, @Param("accountId") Integer accountId,
                                    @Param("type") TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.type = :type " +
            "AND t.occurredAt >= :from AND t.occurredAt < :to")
    BigDecimal sumByUserIdTypeAndDateRange(@Param("userId") Integer userId, @Param("type") TransactionType type,
                                           @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT t.categoryId, SUM(t.amount) FROM Transaction t WHERE t.userId = :userId " +
            "AND t.type = :type " +
            "AND t.occurredAt >= :from AND t.occurredAt < :to " +
            "GROUP BY t.categoryId")
    List<Object[]> sumExpenseTransactionsByCategoryInDateRange(@Param("userId") Integer userId,
                                                               @Param("from") LocalDateTime from,
                                                               @Param("to") LocalDateTime to,
                                                               @Param("type") TransactionType type);
}
