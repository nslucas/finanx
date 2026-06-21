package com.example.prospera.DTO;

import com.example.prospera.Entities.CategoryType;

import java.math.BigDecimal;

public record CategorySummaryRecord(Integer categoryId, String categoryName, CategoryType categoryType,
                                    BigDecimal amount) {
}
