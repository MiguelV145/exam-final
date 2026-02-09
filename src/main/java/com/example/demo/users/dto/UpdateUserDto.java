package com.example.demo.users.dto;

import com.example.demo.roles.entity.RoleName;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UpdateUserDto(
    Boolean enabled,
    
    @NotEmpty(message = "Debe proporcionar al menos un rol")
    Set<RoleName> roleNames
) {
}
