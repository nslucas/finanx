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
    public ResponseEntity<Expense> findById(@PathVariable String id){
        Expense obj = service.getExpenseById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<String> createExpense(@RequestBody ExpenseRecord objDTO) {
        Expense expense = service.createExpense(objDTO);
        return ResponseEntity.ok("Expense created successfully.");
    }

}
