package com.example.demo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RegisterRequest(
    @NotBlank String username,
    @Email @NotBlank String email,
    @NotBlank String password,
    Set<String> roles
) {
}
