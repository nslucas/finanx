package com.example.prospera.DTO;

import java.math.BigDecimal;

public record FixedVariableSummaryResponse(Integer month, Integer year, BigDecimal fixedAmount,
                                           BigDecimal variableAmount, BigDecimal unclassifiedAmount) {
}
