package com.example.finanx.Entities;

import java.util.List;

public class MonthlyExpensesResponse {
    private List<Expense> expenses;
    private double totalAmount;

    public MonthlyExpensesResponse(List<Expense> expenses, double totalAmount) {
        this.expenses = expenses;
        this.totalAmount = totalAmount;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
