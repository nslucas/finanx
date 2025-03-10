package com.example.finanx.Resources;

import com.example.finanx.DTO.ExpenseRecord;
import com.example.finanx.Entities.Expense;
import com.example.finanx.Services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(value="/{id}/total-expenses/current-month")
    public ResponseEntity<Double> getTotalExpensesInCurrentMonth(@PathVariable Integer id){
        double retrieveTotalExpenses = service.getTotalExpensesByUserIdInCurrentMonth(id);
        return ResponseEntity.ok().body(retrieveTotalExpenses);
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
