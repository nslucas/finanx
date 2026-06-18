package com.example.finanx.DTO;

import com.example.finanx.Entities.Transaction;
import com.example.finanx.Entities.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRecord(Integer id, TransactionType type, BigDecimal amount, LocalDateTime occurredAt,
                                String description, Integer accountId, Integer relatedTransactionId,
                                Integer categoryId) {
    public TransactionRecord(Integer id, TransactionType type, BigDecimal amount, LocalDateTime occurredAt,
                             String description, Integer accountId, Integer relatedTransactionId) {
        this(id, type, amount, occurredAt, description, accountId, relatedTransactionId, null);
    }

    public TransactionRecord(Transaction transaction) {
        this(transaction.getId(), transaction.getType(), transaction.getAmount(), transaction.getOccurredAt(),
                transaction.getDescription(), transaction.getAccountId(), transaction.getRelatedTransactionId(),
                transaction.getCategoryId());
    }
}
