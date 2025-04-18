package com.example.finanx.DTO;

import com.example.finanx.Entities.Expense;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
public record ExpenseRecord(Integer id, String name, Double amount, Integer installmentCount, LocalDateTime purchaseDate, String description, Integer userId) {

    public ExpenseRecord(Expense expense){
        this(expense.getId(), expense.getName(), expense.getAmount(), expense.getInstallmentCount(), expense.getPurchaseDate(), expense.getDescription(), expense.getUserId());
    }

}
