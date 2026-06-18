package com.example.finanx.DTO;

import java.math.BigDecimal;

public record MonthlySummaryResponse(Integer month, Integer year, BigDecimal incomeTotal,
                                     BigDecimal accountExpenseTotal, BigDecimal cardPaymentsTotal,
                                     BigDecimal netCashFlow, BigDecimal totalAccountBalance,
                                     BigDecimal cardBillsTotal, BigDecimal cardBillsRemaining) {
}
