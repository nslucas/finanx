package com.example.finanx.Resources;

import com.example.finanx.DTO.CardRecord;
import com.example.finanx.DTO.CardStatementResponse;
import com.example.finanx.Entities.Card;
import com.example.finanx.Services.CardService;
import com.example.finanx.Services.CardStatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardResource {
    private final CardService service;
    private final CardStatementService statementService;

    public CardResource(CardService service, CardStatementService statementService) {
        this.service = service;
        this.statementService = statementService;
    }

    @GetMapping
    public ResponseEntity<List<CardRecord>> findAll() {
        List<CardRecord> cards = service.findAllActiveForAuthenticatedUser().stream()
                .map(CardRecord::new)
                .toList();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardRecord> findById(@PathVariable Integer id) {
        Card card = service.findAuthenticatedUserCard(id);
        return ResponseEntity.ok(new CardRecord(card));
    }

    @PostMapping
    public ResponseEntity<CardRecord> create(@RequestBody CardRecord record) {
        Card card = service.create(record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(card.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new CardRecord(card));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardRecord> update(@PathVariable Integer id, @RequestBody CardRecord record) {
        Card card = service.update(id, record);
        return ResponseEntity.ok(new CardRecord(card));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/statements")
    public ResponseEntity<CardStatementResponse> getStatement(@PathVariable Integer id,
                                                              @RequestParam Integer month,
                                                              @RequestParam Integer year) {
        return ResponseEntity.ok(statementService.getStatement(id, month, year));
    }

    @GetMapping("/{id}/statements/current")
    public ResponseEntity<CardStatementResponse> getCurrentStatement(@PathVariable Integer id) {
        return ResponseEntity.ok(statementService.getCurrentStatement(id));
    }
}
