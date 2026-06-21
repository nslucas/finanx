package com.example.prospera.DTO;

import com.example.prospera.Entities.User;

import java.math.BigDecimal;

public record UserRecord(String name, String lastName, BigDecimal monthLimit, String email) {

    public UserRecord(User user) {
        this(user.getName(), user.getLastName(), user.getMonthLimit(), user.getEmail());
    }

}
