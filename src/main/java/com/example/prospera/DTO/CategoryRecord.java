package com.example.prospera.DTO;

import com.example.prospera.Entities.Category;
import com.example.prospera.Entities.CategoryType;

public record CategoryRecord(Integer id, String name, CategoryType type, Boolean active) {
    public CategoryRecord(Category category) {
        this(category.getId(), category.getName(), category.getType(), category.getActive());
    }
}
