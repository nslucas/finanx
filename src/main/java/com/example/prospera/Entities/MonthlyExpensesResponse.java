package com.example.prospera.Entities;

import java.math.BigDecimal;
import java.util.List;

public class MonthlyExpensesResponse {
    private List<Expense> expenses;
    private BigDecimal totalAmount;

    public MonthlyExpensesResponse(List<Expense> expenses, BigDecimal totalAmount) {
        this.expenses = expenses;
        this.totalAmount = totalAmount;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
