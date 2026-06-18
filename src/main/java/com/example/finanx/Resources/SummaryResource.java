package com.example.finanx.Resources;

import com.example.finanx.DTO.MonthlySummaryResponse;
import com.example.finanx.Services.MonthlySummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
public class SummaryResource {
    private final MonthlySummaryService service;

    public SummaryResource(MonthlySummaryService service) {
        this.service = service;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> monthly(@RequestParam Integer month, @RequestParam Integer year) {
        return ResponseEntity.ok(service.getMonthlySummary(month, year));
    }
}
