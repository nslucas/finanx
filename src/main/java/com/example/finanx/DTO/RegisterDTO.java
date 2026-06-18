package com.example.finanx.DTO;

import com.example.finanx.Entities.UserRole;

import java.math.BigDecimal;

public record RegisterDTO(String name, String lastName, BigDecimal monthLimit, String email, String password, UserRole role) {

}
