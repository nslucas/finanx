package com.example.prospera.DTO;

import com.example.prospera.Entities.ExpenseShareStatus;
import com.example.prospera.Entities.SettlementDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SettlementItemRecord(Integer shareId, Integer expenseId, String expenseName, BigDecimal expenseAmount,
                                   Integer creatorUserId, String creatorName, Integer participantUserId,
                                   String participantName, BigDecimal participantAmount, SettlementDirection direction,
                                   ExpenseShareStatus status, LocalDateTime createdAt, LocalDateTime settledAt) {
}
