package com.example.finanx.dto;

import com.example.finanx.entities.User;

public record ExpenseRecord(Long id, Double amount, String name, String description, User user) {
}
