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

    /*public Expense update(Expense obj){
        Expense newObj = expenseRepository.getReferenceById(obj.getId());
        updateData(newObj, obj);
        return expenseRepository.save(newObj);
    }

    private void updateData(Expense newObj, Expense obj) {
        newObj.setName(obj.getName());
        newObj.setAmount(obj.getAmount());
        newObj.setInstallmentCount(obj.getInstallmentCount());
        newObj.setPurchaseDate(obj.getPurchaseDate());
        newObj.setDescription(obj.getDescription());
    }

     */


    public void delete(String id) {
        expenseRepository.deleteById(id);
    }

    public Expense fromDTO(ExpenseRecord objDTO, User user){
        return new Expense(objDTO.id(), objDTO.name(), objDTO.amount(), objDTO.installmentCount(), objDTO.purchaseDate(),objDTO.description(), objDTO.userId());
    }

}









