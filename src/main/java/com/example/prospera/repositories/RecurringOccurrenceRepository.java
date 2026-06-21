package com.example.prospera.repositories;

import com.example.prospera.Entities.RecurringClassification;
import com.example.prospera.Entities.RecurringOccurrence;
import com.example.prospera.Entities.RecurringOccurrenceStatus;
import com.example.prospera.Entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringOccurrenceRepository extends JpaRepository<RecurringOccurrence, Integer> {
    Optional<RecurringOccurrence> findByRecurrenceIdAndOccurrenceDate(Integer recurrenceId, LocalDate occurrenceDate);

    List<RecurringOccurrence> findByUserIdAndOccurrenceDateBetween(Integer userId, LocalDate from, LocalDate to);

    @Query("SELECT SUM(t.amount) FROM RecurringOccurrence o, RecurringTransaction r, Transaction t " +
            "WHERE o.recurrenceId = r.id AND o.transactionId = t.id AND o.userId = :userId " +
            "AND o.status = :status AND r.classification = :classification " +
            "AND t.type = :type " +
            "AND MONTH(t.occurredAt) = :month AND YEAR(t.occurredAt) = :year")
    BigDecimal sumMaterializedAccountExpenseByClassification(@Param("userId") Integer userId,
                                                             @Param("classification") RecurringClassification classification,
                                                             @Param("status") RecurringOccurrenceStatus status,
                                                             @Param("type") TransactionType type,
                                                             @Param("month") Integer month,
                                                             @Param("year") Integer year);

    @Query("SELECT SUM(i.installmentAmount) FROM RecurringOccurrence o, RecurringTransaction r, ExpenseInstallment i " +
            "WHERE o.recurrenceId = r.id AND o.expenseId = i.id.expenseId AND o.userId = :userId " +
            "AND o.status = :status AND r.classification = :classification " +
            "AND MONTH(i.dueDate) = :month AND YEAR(i.dueDate) = :year")
    BigDecimal sumMaterializedCardExpenseByClassification(@Param("userId") Integer userId,
                                                          @Param("classification") RecurringClassification classification,
                                                          @Param("status") RecurringOccurrenceStatus status,
                                                          @Param("month") Integer month,
                                                          @Param("year") Integer year);
}
