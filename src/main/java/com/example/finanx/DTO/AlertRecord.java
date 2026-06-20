package com.example.finanx.DTO;

import com.example.finanx.Entities.AlertSeverity;
import com.example.finanx.Entities.AlertType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlertRecord(String key, AlertType type, AlertSeverity severity, String message,
                          String resourceType, Integer resourceId, BigDecimal amount,
                          BigDecimal threshold, BigDecimal percentageUsed, LocalDate dueDate,
                          Integer month, Integer year) {
}
