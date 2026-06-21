package com.example.prospera.DTO;

import com.example.prospera.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionRecord(Integer id, String name, String description,
                                         RecurringTargetType targetType, TransactionType transactionType,
                                         BigDecimal amount, RecurringFrequency frequency,
                                         LocalDate startDate, LocalDate endDate,
                                         Integer dayOfMonth, Integer monthOfYear,
                                         Integer accountId, Integer cardId, Integer categoryId,
                                         Integer installmentCount, RecurringClassification classification,
                                         Boolean active) {
    public RecurringTransactionRecord(RecurringTransaction recurrence) {
        this(recurrence.getId(), recurrence.getName(), recurrence.getDescription(), recurrence.getTargetType(),
                recurrence.getTransactionType(), recurrence.getAmount(), recurrence.getFrequency(),
                recurrence.getStartDate(), recurrence.getEndDate(), recurrence.getDayOfMonth(),
                recurrence.getMonthOfYear(), recurrence.getAccountId(), recurrence.getCardId(),
                recurrence.getCategoryId(), recurrence.getInstallmentCount(), recurrence.getClassification(),
                recurrence.getActive());
    }
}
