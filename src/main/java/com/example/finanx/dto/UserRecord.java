package com.example.finanx.dto;

import com.example.finanx.entities.User;
import com.example.finanx.resources.UserResource;

public record UserRecord(Long id, String name, String lastName, Double monthLimit, String email, String password) {

    public UserRecord(User user) {
        this(user.getId(), user.getName(), user.getLastName(), user.getMonthLimit(), user.getEmail(), user.getPassword());
    }

}