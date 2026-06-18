package com.example.finanx.Resources;

import com.example.finanx.DTO.ExpenseRecord;
import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.MonthlyExpensesResponse;
import com.example.finanx.Services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseResource {

    @Autowired
    private ExpenseService service;

    @GetMapping
    public ResponseEntity<List<ExpenseRecord>> findAll(@RequestParam(required = false) Integer month,
                                                       @RequestParam(required = false) Integer year,
                                                       @RequestParam(required = false) Integer cardId){
        List<ExpenseRecord> list = service.findAuthenticatedUserExpenses(month, year, cardId).stream()
                .map(ExpenseRecord::new)
                .toList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<ExpenseRecord> findById(@PathVariable Integer id){
        Expense obj = service.getAuthenticatedUserExpense(id);
        ExpenseRecord expenseRecord = new ExpenseRecord(obj.getId(),  obj.getName(), obj.getAmount(),
                obj.getInstallmentCount(), obj.getPurchaseDate(), obj.getDescription(), obj.getUserId(),
                obj.getCardId(), obj.getCategoryId());
        return ResponseEntity.ok().body(expenseRecord);
    }

    @GetMapping(value="/{id}/total-expenses")
    public ResponseEntity<BigDecimal> getTotalExpenses(@PathVariable Integer id){
        BigDecimal retrieveTotalExpenses = service.getTotalExpensesByUserId(id);
        return ResponseEntity.ok().body(retrieveTotalExpenses);
    }

    @GetMapping(value="/{id}/total-expenses/current-month")
    public ResponseEntity<MonthlyExpensesResponse> getTotalExpensesInCurrentMonth(@PathVariable Integer id){
        List<Expense> expenses = service.getExpensesByUserIdInCurrentMonth(id);
        BigDecimal totalAmount = service.getTotalExpensesByUserIdInCurrentMonth(id);
        MonthlyExpensesResponse response = new MonthlyExpensesResponse(expenses, totalAmount);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping(value="/{id}/total-expenses/any-month")
    public ResponseEntity<MonthlyExpensesResponse> getTotalExpensesInAnyMonth(@PathVariable Integer id, @RequestParam Integer month, @RequestParam Integer year){
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        if (year > Year.now().getValue()) {
            throw new IllegalArgumentException("Year must be a valid year.");
        }

        List<Expense> expenses = service.getExpensesByUserIdInAnyMonth(id, month, year);
        BigDecimal totalAmount = service.getTotalExpensesByUserIdInAnyMonth(id, month, year);
        MonthlyExpensesResponse response = new MonthlyExpensesResponse(expenses, totalAmount);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<ExpenseRecord> update(@RequestBody ExpenseRecord objDTO, @PathVariable Integer id){
        Expense obj = service.update(id, objDTO);
        return ResponseEntity.ok(new ExpenseRecord(obj));
    }


    @PostMapping
    public ResponseEntity<ExpenseRecord> createExpense(@RequestBody ExpenseRecord objDTO) {
        Expense expense = service.createExpense(objDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(expense.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new ExpenseRecord(expense));
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
