package com.example.finanx.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CardStatementResponse(Integer cardId, String cardName, Integer month, Integer year,
                                    LocalDate dueDate, LocalDate closingDate, BigDecimal totalAmount,
                                    BigDecimal availableLimit, List<ExpenseInstallmentRecord> installments) {
}
