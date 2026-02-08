package com.example.demo.roles.dto;

import com.example.demo.roles.entity.RoleName;
import jakarta.validation.constraints.NotNull;

public record CreateRoleDto(
    @NotNull RoleName name
) {
}
