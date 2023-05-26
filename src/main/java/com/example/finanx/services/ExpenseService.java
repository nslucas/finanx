package com.example.finanx.services;

import com.example.finanx.dto.ExpenseRecord;
import com.example.finanx.entities.Expense;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }


    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Expense not found with id: " + id));
    }

    public Expense insert(Expense obj) {
        return expenseRepository.save(obj);
    }
   public Expense fromDTO(ExpenseRecord objDTO){
        return new Expense(objDTO.id(), objDTO.amount(), objDTO.name(), objDTO.installmentCount(), objDTO.purchaseDate(), objDTO.description(), objDTO.userId());
    }
}
    /*
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


     */


