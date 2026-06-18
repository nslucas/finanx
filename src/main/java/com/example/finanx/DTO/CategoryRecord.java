package com.example.finanx.DTO;

import com.example.finanx.Entities.Category;
import com.example.finanx.Entities.CategoryType;

public record CategoryRecord(Integer id, String name, CategoryType type, Boolean active) {
    public CategoryRecord(Category category) {
        this(category.getId(), category.getName(), category.getType(), category.getActive());
    }
}
