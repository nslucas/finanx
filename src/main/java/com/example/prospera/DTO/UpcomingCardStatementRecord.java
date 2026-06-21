package com.example.prospera.DTO;

import com.example.prospera.Entities.CardStatementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpcomingCardStatementRecord(Integer cardId, String cardName, Integer month, Integer year,
                                          LocalDate dueDate, LocalDate closingDate, BigDecimal totalAmount,
                                          BigDecimal paidAmount, BigDecimal remainingAmount,
                                          CardStatementStatus status) {
}
