package com.example.finanx.Entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExpenseInstallmentId implements Serializable {
    private Integer expenseId;
    private Integer installmentNumber;

    // Construtor padrão necessário para JPA
    public ExpenseInstallmentId() {
    }

    // Construtor com parâmetros
    public ExpenseInstallmentId(Integer expenseId, Integer installmentNumber) {
        this.expenseId = expenseId;
        this.installmentNumber = installmentNumber;
    }

    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    // equals() e hashCode() são OBRIGATÓRIOS para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseInstallmentId that = (ExpenseInstallmentId) o;
        return Objects.equals(expenseId, that.expenseId) &&
                Objects.equals(installmentNumber, that.installmentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId, installmentNumber);
    }
}
