package com.example.finanx.Resources;

import com.example.finanx.DTO.ExpenseRecord;
import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.MonthlyExpensesResponse;
import com.example.finanx.Services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseResource {

    @Autowired
    private ExpenseService service;

    @GetMapping
    public ResponseEntity<List<ExpenseRecord>> findAll(){
        List<ExpenseRecord> list = service.getAllExpenses().stream().map(ExpenseRecord::new).toList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<ExpenseRecord> findById(@PathVariable Integer id){
        Expense obj = service.getExpenseById(id);
        ExpenseRecord expenseRecord = new ExpenseRecord(obj.getId(),  obj.getName(), obj.getAmount(),
                obj.getInstallmentCount(), obj.getPurchaseDate(), obj.getDescription(), obj.getUserId());
        return ResponseEntity.ok().body(expenseRecord);
    }

    @GetMapping(value="/{id}/total-expenses")
    public ResponseEntity<Double> getTotalExpenses(@PathVariable Integer id){
        double retrieveTotalExpenses = service.getTotalExpensesByUserId(id);
        return ResponseEntity.ok().body(retrieveTotalExpenses);
    }


    /*
    TODO:
    Make this method return the sum of expense_installments for current_month
     */
    @GetMapping(value="/{id}/total-expenses/current-month")
    public ResponseEntity<MonthlyExpensesResponse> getTotalExpensesInCurrentMonth(@PathVariable Integer id){
        List<Expense> expenses = service.getExpensesByUserIdInCurrentMonth(id);
        double totalAmount = service.getTotalExpensesByUserIdInCurrentMonth(id);
        MonthlyExpensesResponse response = new MonthlyExpensesResponse(expenses, totalAmount);
        return ResponseEntity.ok().body(response);
    }

    /*
    TODO:
    Make this method return the sum of expense_installments for the due month
     */
    @GetMapping(value="/{id}/total-expenses/any-month")
    public ResponseEntity<MonthlyExpensesResponse> getTotalExpensesInAnyMonth(@PathVariable Integer id, @RequestParam Integer month, @RequestParam Integer year){
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year > Year.now().getValue()) {
            throw new IllegalArgumentException("Year must be a valid year.");
        }

        List<Expense> expenses = service.getExpensesByUserIdInAnyMonth(id, month, year);
        double totalAmount = service.getTotalExpensesByUserIdInAnyMonth(id, month, year);
        MonthlyExpensesResponse response = new MonthlyExpensesResponse(expenses, totalAmount);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<Expense> update(@RequestBody ExpenseRecord objDTO, @PathVariable Integer id){
        Expense obj = service.fromDTO(objDTO);
        obj.setId(id);
        obj = service.update(obj);
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    public ResponseEntity<String> createExpense(@RequestBody ExpenseRecord objDTO) {
        Expense expense = service.createExpense(objDTO);

        return ResponseEntity.ok("Expense created successfully.");
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
