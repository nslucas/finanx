package com.example.prospera.DTO;

import com.example.prospera.Entities.AlertSeverity;
import com.example.prospera.Entities.AlertType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AlertRecord(String key, AlertType type, AlertSeverity severity, String message,
                          String resourceType, Integer resourceId, BigDecimal amount,
                          BigDecimal threshold, BigDecimal percentageUsed, LocalDate dueDate,
                          Integer month, Integer year) {
}
