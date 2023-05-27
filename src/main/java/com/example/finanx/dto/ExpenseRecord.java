package com.example.finanx.dto;

import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;


import java.util.Date;

public record ExpenseRecord(String id, Double amount, String name, Integer installmentCount, Date purchaseDate, String description, UserRecord user) {

    public ExpenseRecord(Expense expense){
        this(expense.getId(), expense.getAmount(), expense.getName(), expense.getInstallmentCount(), expense.getPurchaseDate(), expense.getDescription(), new UserRecord(expense.getUser()));
    }

    public ExpenseRecord(String id, Double amount, String name, Integer installmentCount, Date purchaseDate, String description, User user) {
        this(
                id,
                amount,
                name,
                installmentCount,
                purchaseDate,
                description,
                new UserRecord(user)
        );
    }
}
