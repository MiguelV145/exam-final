package com.example.demo.asesorias.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAsesoriaDto(
    @NotNull(message = "Programmer ID es requerido")
    Long programmerId,
    
    @NotNull(message = "Date es requerido")
    LocalDate date,
    
    @NotNull(message = "Time es requerido")
    LocalTime time,
    
    @Size(min = 5, max = 1000, message = "Comment debe tener entre 5 y 1000 caracteres")
    String comment
) {
}
