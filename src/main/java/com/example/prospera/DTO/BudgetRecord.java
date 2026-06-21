package com.example.prospera.DTO;

import com.example.prospera.Entities.Budget;

import java.math.BigDecimal;

public record BudgetRecord(Integer id, Integer categoryId, Integer month, Integer year,
                           BigDecimal amount, Boolean active) {
    public BudgetRecord(Budget budget) {
        this(budget.getId(), budget.getCategoryId(), budget.getMonth(), budget.getYear(),
                budget.getAmount(), budget.getActive());
    }
}
