package com.example.finanx.DTO;

import com.example.finanx.Entities.UserRole;

public record RegisterDTO(String name, String lastName, Double monthLimit, String email, String password, UserRole role) {

}
