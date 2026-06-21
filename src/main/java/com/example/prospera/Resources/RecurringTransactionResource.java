package com.example.prospera.Resources;

import com.example.prospera.DTO.RecurringOccurrenceRecord;
import com.example.prospera.DTO.RecurringOccurrenceRequest;
import com.example.prospera.DTO.RecurringTransactionRecord;
import com.example.prospera.Entities.RecurringOccurrence;
import com.example.prospera.Entities.RecurringTransaction;
import com.example.prospera.Services.RecurringTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/recurrences")
public class RecurringTransactionResource {
    private final RecurringTransactionService service;

    public RecurringTransactionResource(RecurringTransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransactionRecord>> findAll() {
        List<RecurringTransactionRecord> recurrences = service.findAllActiveForAuthenticatedUser().stream()
                .map(RecurringTransactionRecord::new)
                .toList();
        return ResponseEntity.ok(recurrences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionRecord> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(new RecurringTransactionRecord(service.findAuthenticatedUserRecurrence(id)));
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionRecord> create(@RequestBody RecurringTransactionRecord record) {
        RecurringTransaction recurrence = service.create(record);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(recurrence.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new RecurringTransactionRecord(recurrence));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionRecord> update(@PathVariable Integer id,
                                                             @RequestBody RecurringTransactionRecord record) {
        return ResponseEntity.ok(new RecurringTransactionRecord(service.update(id, record)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/occurrences")
    public ResponseEntity<List<RecurringOccurrenceRecord>> occurrences(@RequestParam LocalDate from,
                                                                       @RequestParam LocalDate to) {
        return ResponseEntity.ok(service.previewOccurrences(from, to));
    }

    @PostMapping("/{id}/occurrences")
    public ResponseEntity<RecurringOccurrenceRecord> materialize(@PathVariable Integer id,
                                                                 @RequestBody RecurringOccurrenceRequest request) {
        RecurringOccurrence occurrence = service.materialize(id, request);
        RecurringTransaction recurrence = service.findAuthenticatedUserRecurrence(id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{occurrenceId}")
                .buildAndExpand(occurrence.getId())
                .toUri();
        return ResponseEntity.created(uri)
                .body(new RecurringOccurrenceRecord(recurrence, occurrence, occurrence.getOccurrenceDate()));
    }

    @PostMapping("/{id}/occurrences/skip")
    public ResponseEntity<RecurringOccurrenceRecord> skip(@PathVariable Integer id,
                                                          @RequestBody RecurringOccurrenceRequest request) {
        RecurringOccurrence occurrence = service.skip(id, request);
        RecurringTransaction recurrence = service.findAuthenticatedUserRecurrence(id);
        return ResponseEntity.ok(new RecurringOccurrenceRecord(recurrence, occurrence, occurrence.getOccurrenceDate()));
    }
}
