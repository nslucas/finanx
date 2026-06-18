package com.example.finanx.DTO;

import com.example.finanx.Entities.CategoryType;

import java.math.BigDecimal;

public record CategorySummaryRecord(Integer categoryId, String categoryName, CategoryType categoryType,
                                    BigDecimal amount) {
}
