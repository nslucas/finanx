package com.example.finanx.resources;

import com.example.finanx.dto.ExpenseRecord;
import com.example.finanx.entities.Expense;
import com.example.finanx.entities.User;
import com.example.finanx.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseResource {

    @Autowired
    private ExpenseService service;

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
    public ResponseEntity<Void> insert(@RequestBody ExpenseRecord objDTO) {
        Expense obj = service.fromDTO(objDTO);
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

}
