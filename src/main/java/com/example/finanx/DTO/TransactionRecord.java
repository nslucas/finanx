package com.example.finanx.DTO;

import com.example.finanx.Entities.Transaction;
import com.example.finanx.Entities.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRecord(Integer id, TransactionType type, BigDecimal amount, LocalDateTime occurredAt,
                                String description, Integer accountId, Integer relatedTransactionId) {
    public TransactionRecord(Transaction transaction) {
        this(transaction.getId(), transaction.getType(), transaction.getAmount(), transaction.getOccurredAt(),
                transaction.getDescription(), transaction.getAccountId(), transaction.getRelatedTransactionId());
    }
}
