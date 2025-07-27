package com.example.BlogOnline.DTO;



import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDTO (@NotBlank String username,  String password) {
}

