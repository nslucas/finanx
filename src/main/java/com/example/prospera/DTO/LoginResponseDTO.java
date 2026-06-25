package com.example.prospera.DTO;

public record LoginResponseDTO(String token, Integer userId, String email, String name, String lastName) {
}
