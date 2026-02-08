package com.example.demo.users.dto;

import com.example.demo.roles.entity.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record CreateUserDto(
    @NotBlank String username,
    @Email @NotBlank String email,
    @NotBlank String password,
    Boolean enabled,
    Set<RoleName> roleNames
) {
}
