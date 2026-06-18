package com.example.finanx.Resources;

import com.example.finanx.DTO.*;
import com.example.finanx.Services.FinancialReportService;
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
    private final FinancialReportService financialReportService;

    public SummaryResource(MonthlySummaryService service, SpendingInsightService spendingInsightService,
                           FinancialReportService financialReportService) {
        this.service = service;
        this.spendingInsightService = spendingInsightService;
        this.financialReportService = financialReportService;
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

    @GetMapping("/upcoming")
    public ResponseEntity<UpcomingSummaryResponse> upcoming(@RequestParam java.time.LocalDate from,
                                                            @RequestParam java.time.LocalDate to) {
        return ResponseEntity.ok(financialReportService.getUpcoming(from, to));
    }

    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponse> forecast(@RequestParam(required = false) Integer months) {
        return ResponseEntity.ok(financialReportService.getForecast(months));
    }

    @GetMapping("/yearly")
    public ResponseEntity<YearlySummaryResponse> yearly(@RequestParam Integer year) {
        return ResponseEntity.ok(financialReportService.getYearlySummary(year));
    }

    @GetMapping("/cards")
    public ResponseEntity<java.util.List<CardMonthlySummaryRecord>> cards(@RequestParam Integer month,
                                                                          @RequestParam Integer year) {
        return ResponseEntity.ok(financialReportService.getCardsSummary(month, year));
    }

    @GetMapping("/fixed-variable")
    public ResponseEntity<FixedVariableSummaryResponse> fixedVariable(@RequestParam Integer month,
                                                                      @RequestParam Integer year) {
        return ResponseEntity.ok(financialReportService.getFixedVariableSummary(month, year));
    }
}
