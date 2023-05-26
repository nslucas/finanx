package com.example.finanx.resources;

import com.example.finanx.dto.ExpenseRecord;
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
    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<Expense>> findAll(){
        List<Expense> list = service.getAllExpenses();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value="/id")
    public ResponseEntity<Expense> findById(@PathVariable Long id){
        Expense obj = service.getExpenseById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<String> createExpense(@RequestBody ExpenseRecord objDTO, @RequestParam Long userId) {
        Long user = userService.findById(userId).getId();
        Expense expense = new Expense();
        expense.setAmount(objDTO.amount());
        expense.setName(objDTO.name());
        expense.setInstallmentCount(objDTO.installmentCount());
        expense.setPurchaseDate(objDTO.purchaseDate());
        expense.setDescription(objDTO.description());
        expense.setUserId(user);
        service.insert(expense);
        return ResponseEntity.ok("Expense created successfully.");
    }

}
