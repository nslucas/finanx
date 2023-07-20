package com.example.finanx.resources;

import com.example.finanx.dto.ExpenseRecord;
import com.example.finanx.dto.UserRecord;
import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;
import com.example.finanx.exception.ObjectNotFoundException;
import com.example.finanx.services.ExpenseService;
import com.example.finanx.services.UserService;
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
    public ResponseEntity<ExpenseRecord> findById(@PathVariable String id){
        Expense obj = service.getExpenseById(id);
        ExpenseRecord expenseRecord = new ExpenseRecord(obj.getId(),  obj.getName(), obj.getAmount(),
                obj.getInstallmentCount(), obj.getPurchaseDate(), obj.getDescription(), obj.getUserId());
        return ResponseEntity.ok().body(expenseRecord);
    }

    /*@PutMapping(value="/{id}")
    public ResponseEntity<Expense> update(@RequestBody ExpenseRecord objDTO, @PathVariable String id){
        Expense obj = service.fromDTO(objDTO);
        obj.setId(id);
        obj = service.update(obj);
        return ResponseEntity.noContent().build();

    }
    */

    @PostMapping
    public ResponseEntity<String> createExpense(@RequestBody ExpenseRecord objDTO) {
        Expense expense = service.createExpense(objDTO);
        return ResponseEntity.ok("Expense created successfully.");
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
