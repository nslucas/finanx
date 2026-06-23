package com.example.prospera.DTO;

import com.example.prospera.Entities.SettlementDirection;

import java.math.BigDecimal;

public record SettlementSummaryRecord(Integer counterpartyUserId, String counterpartyName, BigDecimal amount,
                                      SettlementDirection direction) {
}
