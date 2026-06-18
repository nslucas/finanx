package com.example.finanx.DTO;

import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.ExpenseInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseInstallmentRecord(Integer expenseId, Integer installmentNumber, String expenseName,
                                       BigDecimal amount, LocalDate dueDate, Integer cardId) {
    public ExpenseInstallmentRecord(ExpenseInstallment installment, Expense expense) {
        this(installment.getExpenseId(), installment.getInstallmentNumber(), expense.getName(),
                installment.getInstallment_amount(), installment.getDueDate(), expense.getCardId());
    }
}
