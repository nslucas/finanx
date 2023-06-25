package com.example.finanx.services;

import com.example.finanx.dto.ExpenseRecord;
import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;
import com.example.finanx.exception.LimitExceededException;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.repositories.ExpenseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
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
        User user =  userService.findById(obj.userId());
        //double totalExpenses = calculateTotalExpenses(user);
        double newExpenseAmount = obj.amount();
        //double expenseLimit = user.getMonthLimit();
        //if (totalExpenses + newExpenseAmount > expenseLimit){
          //  throw new LimitExceededException("New expense exceeds the month limit expenses for this user!");
        //}// else {
            Expense expense = fromDTO(obj, user);
            return expenseRepository.save(expense);
        //}
    }


    /*public double calculateTotalExpenses(User user) {
        // Obtém o mês atual
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Mês atual (lembrando que o mês inicia em 0)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR); // Ano atual

        // Busca as despesas do usuário no mês atual
        List<Expense> expenses = expenseRepository.findExpensesByUserAndMonth(user, currentMonth, currentYear);

        // Calcula o total de despesas
        double totalExpenses = 0.0;
        for (Expense expense : expenses) {
            totalExpenses += expense.getAmount();
        }

        return totalExpenses;
    }
     */

       public Expense fromDTO(ExpenseRecord objDTO, User user){
        return new Expense(objDTO.id(), objDTO.amount(), objDTO.name(), objDTO.installmentCount(), objDTO.purchaseDate(),objDTO.description(), objDTO.userId());
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


