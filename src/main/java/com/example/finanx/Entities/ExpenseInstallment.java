package com.example.finanx.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_installment")
public class ExpenseInstallment {
    @EmbeddedId
    private ExpenseInstallmentId id;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "installment_amount")
    private Double installmentAmount;


    public ExpenseInstallment() {
    }

    public ExpenseInstallment(Integer expenseId, Integer installmentNumber,
                              Double installmentAmount, LocalDate dueDate) {
        this.id = new ExpenseInstallmentId(expenseId, installmentNumber);
        this.installmentAmount = installmentAmount;
        this.dueDate = dueDate;
    }

    public ExpenseInstallmentId getId() {
        return id;
    }

    public Integer getExpenseId() {
        return id != null ? id.getExpenseId() : null;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getInstallment_amount() {
        return installmentAmount;
    }

    public void setInstallment_amount(Double installment_amount) {
        this.installmentAmount = installment_amount;
    }

    public void setExpenseId(Integer expenseId) {
        if (this.id == null) {
            this.id = new ExpenseInstallmentId();
        }
        this.id.setExpenseId(expenseId);
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        if (this.id == null) {
            this.id = new ExpenseInstallmentId();
        }
        this.id.setInstallmentNumber(installmentNumber);
    }

}
