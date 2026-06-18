package com.example.finanx.DTO;

import com.example.finanx.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringOccurrenceRecord(Integer id, Integer recurrenceId, String recurrenceName,
                                        LocalDate occurrenceDate, BigDecimal amount,
                                        RecurringTargetType targetType, TransactionType transactionType,
                                        Integer accountId, Integer cardId, Integer categoryId,
                                        RecurringClassification classification,
                                        RecurringOccurrenceStatus status, Integer transactionId,
                                        Integer expenseId) {
    public RecurringOccurrenceRecord(RecurringTransaction recurrence, RecurringOccurrence occurrence,
                                     LocalDate occurrenceDate) {
        this(occurrence == null ? null : occurrence.getId(), recurrence.getId(), recurrence.getName(),
                occurrenceDate, recurrence.getAmount(), recurrence.getTargetType(), recurrence.getTransactionType(),
                recurrence.getAccountId(), recurrence.getCardId(), recurrence.getCategoryId(),
                recurrence.getClassification(),
                occurrence == null ? RecurringOccurrenceStatus.PENDING : occurrence.getStatus(),
                occurrence == null ? null : occurrence.getTransactionId(),
                occurrence == null ? null : occurrence.getExpenseId());
    }
}
