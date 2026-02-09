package com.example.demo.asesorias.dto;

import com.example.demo.asesorias.entity.AsesoriaStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAsesoriaStatusDto(
    @NotNull(message = "Status es requerido")
    AsesoriaStatus status,
    
    @Size(min = 5, max = 1000, message = "Response message debe tener entre 5 y 1000 caracteres")
    String responseMsg
) {
}
