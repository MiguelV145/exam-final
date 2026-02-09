package com.example.demo.asesorias.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateAsesoriaDto(
    LocalDate date,
    LocalTime time,
    
    @Size(min = 5, max = 1000, message = "Comment debe tener entre 5 y 1000 caracteres")
    String comment
) {
}
