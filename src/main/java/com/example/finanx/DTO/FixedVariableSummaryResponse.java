package com.example.finanx.DTO;

import java.math.BigDecimal;

public record FixedVariableSummaryResponse(Integer month, Integer year, BigDecimal fixedAmount,
                                           BigDecimal variableAmount, BigDecimal unclassifiedAmount) {
}
