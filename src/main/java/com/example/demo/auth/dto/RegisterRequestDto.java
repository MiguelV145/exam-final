package com.example.demo.auth.dto;

import com.example.demo.roles.entity.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
    @NotBlank String username,
    @Email @NotBlank String email,
    @NotBlank String password,
    String displayName,
    RoleName role
) {
}
