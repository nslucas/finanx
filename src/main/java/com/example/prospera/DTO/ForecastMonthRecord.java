package com.example.prospera.DTO;

import java.math.BigDecimal;

public record ForecastMonthRecord(Integer month, Integer year, BigDecimal incomeTotal,
                                  BigDecimal accountExpenseTotal, BigDecimal cardStatementExpenseTotal,
                                  BigDecimal cardPaymentsTotal, BigDecimal recurringIncomeTotal,
                                  BigDecimal recurringAccountExpenseTotal, BigDecimal recurringCardExpenseTotal,
                                  BigDecimal projectedNetCashFlow, BigDecimal projectedAccountBalance) {
}
