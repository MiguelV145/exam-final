package com.example.demo.users.dto;

import com.example.demo.roles.entity.RoleName;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * DTO para asignar roles a un usuario.
 * Se puede enviar una lista de roles que reemplazarán los actuales.
 */
public record AssignRolesDto(
    @NotEmpty(message = "roleNames no puede estar vacío")
    Set<RoleName> roleNames
) {
}
