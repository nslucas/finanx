package com.example.prospera.Resources;

import com.example.prospera.DTO.CardRecord;
import com.example.prospera.DTO.CardPaymentRecord;
import com.example.prospera.DTO.CardStatementResponse;
import com.example.prospera.Entities.Card;
import com.example.prospera.Entities.CardPayment;
import com.example.prospera.Services.CardPaymentService;
import com.example.prospera.Services.CardService;
import com.example.prospera.Services.CardStatementService;
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
    private final CardPaymentService paymentService;

    public CardResource(CardService service, CardStatementService statementService, CardPaymentService paymentService) {
        this.service = service;
        this.statementService = statementService;
        this.paymentService = paymentService;
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

    @PostMapping("/{id}/payments")
    public ResponseEntity<CardPaymentRecord> createPayment(@PathVariable Integer id, @RequestBody CardPaymentRecord record) {
        CardPayment payment = paymentService.create(id, record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{paymentId}")
                .buildAndExpand(payment.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new CardPaymentRecord(payment));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<CardPaymentRecord>> findPayments(@PathVariable Integer id,
                                                                @RequestParam Integer month,
                                                                @RequestParam Integer year) {
        List<CardPaymentRecord> payments = paymentService.findPayments(id, month, year).stream()
                .map(CardPaymentRecord::new)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}/payments/{paymentId}")
    public ResponseEntity<CardPaymentRecord> updatePayment(@PathVariable Integer id,
                                                           @PathVariable Integer paymentId,
                                                           @RequestBody CardPaymentRecord record) {
        CardPayment payment = paymentService.update(id, paymentId, record);
        return ResponseEntity.ok(new CardPaymentRecord(payment));
    }

    @DeleteMapping("/{id}/payments/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id, @PathVariable Integer paymentId) {
        paymentService.delete(id, paymentId);
        return ResponseEntity.noContent().build();
    }
}
