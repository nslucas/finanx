package com.example.finanx.Repositories;

import com.example.finanx.Entities.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Integer> {
    List<RecurringTransaction> findByUserIdAndActiveTrueOrderByNameAsc(Integer userId);

    Optional<RecurringTransaction> findByIdAndUserId(Integer id, Integer userId);

    @Query("SELECT r FROM RecurringTransaction r WHERE r.userId = :userId AND r.active = true " +
            "AND r.startDate <= :to AND (r.endDate IS NULL OR r.endDate >= :from) " +
            "ORDER BY r.name ASC")
    List<RecurringTransaction> findActiveInRange(@Param("userId") Integer userId,
                                                 @Param("from") LocalDate from,
                                                 @Param("to") LocalDate to);
}
