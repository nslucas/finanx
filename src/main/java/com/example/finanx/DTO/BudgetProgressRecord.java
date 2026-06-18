package com.example.finanx.DTO;

import com.example.finanx.Entities.BudgetStatus;

import java.math.BigDecimal;

public record BudgetProgressRecord(Integer budgetId, Integer categoryId, String categoryName,
                                   Integer month, Integer year, BigDecimal budgetAmount,
                                   BigDecimal spentAmount, BigDecimal remainingAmount,
                                   BigDecimal percentUsed, BudgetStatus status) {
}
