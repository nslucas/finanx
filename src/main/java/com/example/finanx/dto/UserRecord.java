package com.example.finanx.dto;

import com.example.finanx.entities.User;
import com.example.finanx.resources.UserResource;

public record UserRecord(String name, String lastName, Double monthLimit, String email) {

    public UserRecord(User user) {
        this(user.getName(), user.getLastName(), user.getMonthLimit(), user.getEmail());
    }

}