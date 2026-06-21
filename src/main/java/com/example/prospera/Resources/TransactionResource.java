package com.example.prospera.Resources;

import com.example.prospera.DTO.TransactionRecord;
import com.example.prospera.Entities.Transaction;
import com.example.prospera.Entities.TransactionType;
import com.example.prospera.Services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionResource {
    private final TransactionService service;

    public TransactionResource(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TransactionRecord>> findAll(@RequestParam(required = false) Integer month,
                                                           @RequestParam(required = false) Integer year,
                                                           @RequestParam(required = false) Integer accountId,
                                                           @RequestParam(required = false) TransactionType type) {
        List<TransactionRecord> transactions = service.findAuthenticatedUserTransactions(month, year, accountId, type)
                .stream()
                .map(TransactionRecord::new)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionRecord> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(new TransactionRecord(service.findAuthenticatedUserTransaction(id)));
    }

    @PostMapping
    public ResponseEntity<TransactionRecord> create(@RequestBody TransactionRecord record) {
        Transaction transaction = service.create(record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transaction.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new TransactionRecord(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
