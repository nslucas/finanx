package com.example.prospera.DTO;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(Integer month, Integer year, BigDecimal incomeTotal,
                                     BigDecimal accountExpenseTotal, BigDecimal cardPaymentsTotal,
                                     BigDecimal netCashFlow, BigDecimal totalAccountBalance,
                                     BigDecimal cardBillsTotal, BigDecimal cardBillsRemaining,
                                     List<CategorySummaryRecord> categoryBreakdown,
                                     List<BudgetProgressRecord> budgetProgress) {
    public MonthlySummaryResponse(Integer month, Integer year, BigDecimal incomeTotal,
                                  BigDecimal accountExpenseTotal, BigDecimal cardPaymentsTotal,
                                  BigDecimal netCashFlow, BigDecimal totalAccountBalance,
                                  BigDecimal cardBillsTotal, BigDecimal cardBillsRemaining) {
        this(month, year, incomeTotal, accountExpenseTotal, cardPaymentsTotal, netCashFlow,
                totalAccountBalance, cardBillsTotal, cardBillsRemaining, List.of(), List.of());
    }
}
