package com.example.prospera.repositories;

import com.example.prospera.Entities.ExpenseShare;
import com.example.prospera.Entities.ExpenseShareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Integer> {
    Optional<ExpenseShare> findByExpenseId(Integer expenseId);

    List<ExpenseShare> findByExpenseIdIn(List<Integer> expenseIds);

    List<ExpenseShare> findByCreatorUserIdOrParticipantUserId(Integer creatorUserId, Integer participantUserId);

    @Query("SELECT s FROM ExpenseShare s WHERE " +
            "(s.creatorUserId = :userId OR s.participantUserId = :userId) " +
            "AND (:counterpartyUserId IS NULL OR s.creatorUserId = :counterpartyUserId " +
            "OR s.participantUserId = :counterpartyUserId) " +
            "ORDER BY s.createdAt DESC, s.id DESC")
    List<ExpenseShare> findSettlementItems(@Param("userId") Integer userId,
                                           @Param("counterpartyUserId") Integer counterpartyUserId);

    @Query("SELECT s FROM ExpenseShare s WHERE s.id = :id " +
            "AND (s.creatorUserId = :userId OR s.participantUserId = :userId)")
    Optional<ExpenseShare> findByIdForUser(@Param("id") Integer id, @Param("userId") Integer userId);

    boolean existsByExpenseIdAndStatus(Integer expenseId, ExpenseShareStatus status);
}
