package com.example.finanx.dto;

import com.example.finanx.entities.User;


import java.util.Date;

public record ExpenseRecord(Long id, Double amount, String name, Integer installmentCount, Date purchaseDate, String description, Long userId) {
}
