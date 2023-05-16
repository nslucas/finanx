package com.example.finanx.services;

import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;
import com.example.finanx.exception.LimitExceededException;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<User> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public User getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Expense not found with id: " + id));
    }

    public Expense createExpense(Expense expense) {
        validateExpenseValue(expense.getAmount(), expense.getUser().getMonthLimit());

        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense expense = getExpenseById(id);
        validateExpenseValue(updatedExpense.getAmount(), expense.getUser().getMonthLimit());

        expense.setAmount(updatedExpense.getAmount());
        expense.setName(updatedExpense.getName());
        expense.setDescription(updatedExpense.getDescription());
        expense.setInstallmentCount(updatedExpense.getInstallmentCount());
        expense.setPurchaseDate(updatedExpense.getPurchaseDate());

        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    private void validateExpenseValue(BigDecimal expenseValue, Double monthLimit) {
        BigDecimal currentMonthExpenses = expenseRepository.sumExpensesByUserAndMonth(expenseValue, LocalDate.now());

        if (currentMonthExpenses.add(expenseValue).compareTo(BigDecimal.valueOf(monthLimit)) > 0) {
            throw new LimitExceededException("Expense value exceeds the monthly limit.");
        }
    }
}
