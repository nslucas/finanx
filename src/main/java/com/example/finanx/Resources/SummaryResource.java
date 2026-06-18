package com.example.finanx.Resources;

import com.example.finanx.DTO.MonthlySummaryResponse;
import com.example.finanx.DTO.CategorySummaryRecord;
import com.example.finanx.DTO.MonthlyTrendRecord;
import com.example.finanx.Services.MonthlySummaryService;
import com.example.finanx.Services.SpendingInsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
public class SummaryResource {
    private final MonthlySummaryService service;
    private final SpendingInsightService spendingInsightService;

    public SummaryResource(MonthlySummaryService service, SpendingInsightService spendingInsightService) {
        this.service = service;
        this.spendingInsightService = spendingInsightService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> monthly(@RequestParam Integer month, @RequestParam Integer year) {
        return ResponseEntity.ok(service.getMonthlySummary(month, year));
    }

    @GetMapping("/categories")
    public ResponseEntity<java.util.List<CategorySummaryRecord>> categories(@RequestParam Integer month,
                                                                            @RequestParam Integer year) {
        return ResponseEntity.ok(spendingInsightService.getCategorySummary(month, year));
    }

    @GetMapping("/trends")
    public ResponseEntity<java.util.List<MonthlyTrendRecord>> trends(@RequestParam Integer fromMonth,
                                                                     @RequestParam Integer fromYear,
                                                                     @RequestParam Integer toMonth,
                                                                     @RequestParam Integer toYear) {
        return ResponseEntity.ok(spendingInsightService.getTrends(fromMonth, fromYear, toMonth, toYear));
    }
}
