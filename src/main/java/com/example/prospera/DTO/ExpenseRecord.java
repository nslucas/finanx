package com.example.prospera.DTO;

import com.example.prospera.Entities.Expense;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
public record ExpenseRecord(Integer id, String name, BigDecimal amount, Integer installmentCount,
                            LocalDateTime purchaseDate, String description, Integer userId, Integer cardId,
                            Integer categoryId, ExpenseShareRequest share) {

    public ExpenseRecord(Integer id, String name, BigDecimal amount, Integer installmentCount,
                         LocalDateTime purchaseDate, String description, Integer userId, Integer cardId) {
        this(id, name, amount, installmentCount, purchaseDate, description, userId, cardId, null, null);
    }

    public ExpenseRecord(Integer id, String name, BigDecimal amount, Integer installmentCount,
                         LocalDateTime purchaseDate, String description, Integer userId, Integer cardId,
                         Integer categoryId) {
        this(id, name, amount, installmentCount, purchaseDate, description, userId, cardId, categoryId, null);
    }

    public ExpenseRecord(Expense expense){
        this(expense.getId(), expense.getName(), expense.getAmount(), expense.getInstallmentCount(),
                expense.getPurchaseDate(), expense.getDescription(), expense.getUserId(), expense.getCardId(),
                expense.getCategoryId(), null);
    }

}
