package com.example.prospera.DTO;

import com.example.prospera.Entities.CardStatementStatus;

import java.math.BigDecimal;

public record CardMonthlySummaryRecord(Integer cardId, String cardName, Integer month, Integer year,
                                       BigDecimal totalAmount, BigDecimal paidAmount,
                                       BigDecimal remainingAmount, CardStatementStatus status) {
}
