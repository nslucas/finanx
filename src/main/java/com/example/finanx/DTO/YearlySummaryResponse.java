package com.example.finanx.DTO;

import java.math.BigDecimal;
import java.util.List;

public record YearlySummaryResponse(Integer year, List<MonthlyTrendRecord> months,
                                    BigDecimal incomeTotal, BigDecimal accountExpenseTotal,
                                    BigDecimal cardStatementExpenseTotal, BigDecimal netTotal) {
}
