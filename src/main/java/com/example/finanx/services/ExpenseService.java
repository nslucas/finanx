package com.example.finanx.services;

import com.example.finanx.dto.ExpenseRecord;
import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;


    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }


    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }


    public Expense getExpenseById(String id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Expense not found with id: " + id));
    }

        public Expense createExpense(ExpenseRecord obj) {
            User user =  userService.findById(obj.user().id());
            Expense expense = fromDTO(obj, user);
            return expenseRepository.save(expense);
        }
       public Expense fromDTO(ExpenseRecord objDTO, User user){
        return new Expense(objDTO.id(), objDTO.amount(), objDTO.name(), objDTO.installmentCount(), objDTO.purchaseDate(),objDTO.description(), user);
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


