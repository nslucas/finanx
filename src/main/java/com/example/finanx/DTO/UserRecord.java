package com.example.finanx.DTO;

import com.example.finanx.Entities.User;

public record UserRecord(String name, String lastName, Double monthLimit, String email) {

    public UserRecord(User user) {
        this(user.getName(), user.getLastName(), user.getMonthLimit(), user.getEmail());
    }

}