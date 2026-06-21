package com.example.prospera.Resources;

import com.example.prospera.DTO.AlertRecord;
import com.example.prospera.Services.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertResource {
    private final AlertService service;

    public AlertResource(AlertService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AlertRecord>> findAlerts(@RequestParam(required = false) Integer month,
                                                        @RequestParam(required = false) Integer year,
                                                        @RequestParam(required = false) LocalDate from,
                                                        @RequestParam(required = false) LocalDate to) {
        return ResponseEntity.ok(service.getAlerts(month, year, from, to));
    }
}
