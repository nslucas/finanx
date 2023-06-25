package com.example.finanx.dto;

import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;


import java.util.Date;

public record ExpenseRecord(String id, Double amount, String name, Integer installmentCount, Date purchaseDate, String description, Long userId) {

    public ExpenseRecord(Expense expense){
        this(expense.getId(), expense.getAmount(), expense.getName(), expense.getInstallmentCount(), expense.getPurchaseDate(), expense.getDescription(), expense.getUserId());
    }

}
