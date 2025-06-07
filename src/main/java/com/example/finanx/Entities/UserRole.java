package com.example.finanx.Entities;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
