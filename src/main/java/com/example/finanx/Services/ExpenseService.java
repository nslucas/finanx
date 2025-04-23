package com.example.finanx.Services;

import com.example.finanx.DTO.ExpenseRecord;
import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.User;
import com.example.finanx.Exceptions.LimitExceededException;
import com.example.finanx.Exceptions.ObjectNotFoundException;
import com.example.finanx.Repositories.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final String TIME_ZONE = "America/Sao_Paulo";
    private final ExpenseInstallmentService installmentService;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserService userService, ExpenseInstallmentService installmentService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.installmentService = installmentService;
    }


    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }


    public Expense getExpenseById(Integer id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Expense not found with id: " + id));
    }

    public String getSumAmountByUserId(Integer userId) {
        double sumAmountByUser = expenseRepository.sumAmountByUserId(userId);
        User user = userService.findById(userId);
        if (sumAmountByUser >= user.getMonthLimit()) {
            return "Your month limit exceeded, here's your total for this month " + sumAmountByUser;
        }
        return Double.toString(sumAmountByUser);
    }

    public String getSumAmountByUserIdInCurrentMonth(Integer userId) {
        double sumAmountByUser = expenseRepository.sumAmountByUserIdInCurrentMonth(userId);
        User user = userService.findById(userId);
        if (sumAmountByUser >= user.getMonthLimit()) {
            return "Your month limit exceeded, here's your total for this month " + sumAmountByUser;
        }
        return Double.toString(sumAmountByUser);
    }

    public Expense createExpense(ExpenseRecord obj) {
        Expense expense = fromDTO(obj);
        if (obj.purchaseDate() == null) {
            ZoneId zid = ZoneId.of(TIME_ZONE);
            LocalDateTime currentDateTime = LocalDateTime.now(zid);
            expense.setPurchaseDate(currentDateTime);
        }
        expenseRepository.save(expense);
        installmentService.generateInstallments(expense);
        return(expense);
    }

    public Expense update(Expense obj){
        Expense newObj = expenseRepository.getReferenceById(obj.getId());
        newObj.setName(obj.getName());
        newObj.setDescription(obj.getDescription()); // Exemplo: copia a descrição
        newObj.setAmount(obj.getAmount());         // Exemplo: copia o valor
        newObj.setPurchaseDate(obj.getPurchaseDate());
        return expenseRepository.save(newObj);
    }

    public void delete(Integer id) {
        expenseRepository.deleteById(id);
    }

    public Double getTotalExpensesByUserId(Integer userId) {
        return expenseRepository.sumAmountByUserId(userId);
    }

    public Double getTotalExpensesByUserIdInCurrentMonth(Integer userId) {
        return expenseRepository.sumAmountByUserIdInCurrentMonth(userId);
    }

    public Double getTotalExpensesByUserIdInAnyMonth(Integer userId, Integer month, Integer year) {
        return expenseRepository.sumAmountByUserIdInAnyMonth(userId, month, year);
    }

    public List<Expense> getExpensesByUserIdInCurrentMonth(Integer userId) {
        return expenseRepository.findExpensesByUserIdAndPurchaseDateInCurrentMonth(userId);
    }

    public List<Expense> getExpensesByUserIdInAnyMonth(Integer userId, Integer month, Integer year){
        List<Expense> list = expenseRepository.findExpensesByUserIdAndPurchaseDateInAnyMonth(userId, month, year);
        if (list.isEmpty()) {
            throw new ObjectNotFoundException("There are no expenses for this user");
        }
        return list;
    }

    public Expense fromDTO(ExpenseRecord objDTO){
        return new Expense(objDTO.id(), objDTO.name(), objDTO.amount(), objDTO.installmentCount(), objDTO.purchaseDate(),objDTO.description(), objDTO.userId());
    }

}









