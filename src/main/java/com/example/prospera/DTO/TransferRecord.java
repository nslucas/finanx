package com.example.prospera.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferRecord(Integer targetAccountId, BigDecimal amount, LocalDateTime occurredAt, String description) {
}
