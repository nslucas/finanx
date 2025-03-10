package com.example.finanx.DTO;

import com.example.finanx.Entities.Expense;

import java.time.LocalDateTime;

public record ExpenseRecord(Integer id, String name, Double amount, Integer installmentCount, LocalDateTime purchaseDate, String description, Integer userId) {

    public ExpenseRecord(Expense expense){
        this(expense.getId(), expense.getName(), expense.getAmount(), expense.getInstallmentCount(), expense.getPurchaseDate(), expense.getDescription(), expense.getUserId());
    }

}
