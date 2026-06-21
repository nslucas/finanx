package com.example.prospera.DTO;

import com.example.prospera.Entities.UserRole;

import java.math.BigDecimal;

public record RegisterDTO(String name, String lastName, BigDecimal monthLimit, String email, String password, UserRole role) {

}
