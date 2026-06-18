package com.example.finanx.DTO;

import com.example.finanx.Entities.CardStatementStatus;

import java.math.BigDecimal;

public record CardMonthlySummaryRecord(Integer cardId, String cardName, Integer month, Integer year,
                                       BigDecimal totalAmount, BigDecimal paidAmount,
                                       BigDecimal remainingAmount, CardStatementStatus status) {
}
