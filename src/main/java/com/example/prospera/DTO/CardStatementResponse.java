package com.example.prospera.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.example.prospera.Entities.CardStatementStatus;

public record CardStatementResponse(Integer cardId, String cardName, Integer month, Integer year,
                                    LocalDate dueDate, LocalDate closingDate, BigDecimal totalAmount,
                                    BigDecimal availableLimit, BigDecimal paidAmount, BigDecimal remainingAmount,
                                    CardStatementStatus status, List<ExpenseInstallmentRecord> installments) {
}
