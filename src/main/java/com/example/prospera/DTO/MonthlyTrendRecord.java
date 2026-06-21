package com.example.prospera.DTO;

import java.math.BigDecimal;

public record MonthlyTrendRecord(Integer month, Integer year, BigDecimal incomeTotal,
                                 BigDecimal accountExpenseTotal, BigDecimal cardStatementExpenseTotal,
                                 BigDecimal netTotal) {
}
