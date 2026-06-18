package com.example.finanx.Resources;

import com.example.finanx.DTO.AccountRecord;
import com.example.finanx.DTO.TransferRecord;
import com.example.finanx.Entities.Account;
import com.example.finanx.Services.AccountService;
import com.example.finanx.Services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountResource {
    private final AccountService service;
    private final TransactionService transactionService;

    public AccountResource(AccountService service, TransactionService transactionService) {
        this.service = service;
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<AccountRecord>> findAll() {
        List<AccountRecord> accounts = service.findAllActiveForAuthenticatedUser().stream()
                .map(AccountRecord::new)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountRecord> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(new AccountRecord(service.findAuthenticatedUserAccount(id)));
    }

    @PostMapping
    public ResponseEntity<AccountRecord> create(@RequestBody AccountRecord record) {
        Account account = service.create(record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new AccountRecord(account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountRecord> update(@PathVariable Integer id, @RequestBody AccountRecord record) {
        return ResponseEntity.ok(new AccountRecord(service.update(id, record)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/transfers")
    public ResponseEntity<Void> transfer(@PathVariable Integer id, @RequestBody TransferRecord record) {
        transactionService.createTransfer(id, record);
        return ResponseEntity.noContent().build();
    }
}
