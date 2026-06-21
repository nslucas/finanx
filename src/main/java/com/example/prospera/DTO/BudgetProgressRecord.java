package com.example.prospera.DTO;

import com.example.prospera.Entities.BudgetStatus;

import java.math.BigDecimal;

public record BudgetProgressRecord(Integer budgetId, Integer categoryId, String categoryName,
                                   Integer month, Integer year, BigDecimal budgetAmount,
                                   BigDecimal spentAmount, BigDecimal remainingAmount,
                                   BigDecimal percentUsed, BudgetStatus status) {
}
