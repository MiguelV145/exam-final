package com.example.demo.users.dto;

import com.example.demo.roles.entity.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateUserDto(
    @NotBlank(message = "Username no puede estar vacío")
    @Size(min = 3, max = 80, message = "Username debe tener entre 3 y 80 caracteres")
    String username,
    
    @NotBlank(message = "Email no puede estar vacío")
    @Email(message = "Email debe ser válido")
    String email,
    
    @NotBlank(message = "Password no puede estar vacío")
    @Size(min = 6, max = 20, message = "Password debe tener entre 6 y 20 caracteres")
    String password,
    
    Boolean enabled,
    Set<RoleName> roleNames
) {
}
