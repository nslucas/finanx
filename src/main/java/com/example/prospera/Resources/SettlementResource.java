package com.example.prospera.Resources;

import com.example.prospera.DTO.SettlementItemRecord;
import com.example.prospera.DTO.SettlementSummaryRecord;
import com.example.prospera.Services.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/settlements")
public class SettlementResource {
    private final SettlementService service;

    public SettlementResource(SettlementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SettlementSummaryRecord>> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }

    @GetMapping("/items")
    public ResponseEntity<List<SettlementItemRecord>> getItems(@RequestParam(required = false) Integer counterpartyUserId) {
        return ResponseEntity.ok(service.getItems(counterpartyUserId));
    }

    @PostMapping("/items/{shareId}/settle")
    public ResponseEntity<SettlementItemRecord> settle(@PathVariable Integer shareId) {
        return ResponseEntity.ok(service.settle(shareId));
    }
}
