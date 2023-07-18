package com.example.finanx.dto;

import com.example.finanx.entities.Category;
import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public record ExpenseRecord(String id, String name, Double amount, Integer installmentCount, LocalDateTime purchaseDate, String description, Long userId) {

    public ExpenseRecord(Expense expense){
        this(expense.getId(), expense.getName(), expense.getAmount(), expense.getInstallmentCount(), expense.getPurchaseDate(), expense.getDescription(), expense.getUserId());
    }

}
