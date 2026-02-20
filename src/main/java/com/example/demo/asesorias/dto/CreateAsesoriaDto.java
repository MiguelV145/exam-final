package com.example.demo.asesorias.dto;

import com.example.demo.availability.entity.Modality;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateAsesoriaDto(
    @NotNull(message = "Programmer ID es requerido")
    Long programmerId,
    
    @NotNull(message = "startAt es requerido")
    LocalDateTime startAt,
    
    @NotNull(message = "durationMinutes es requerido")
    @Positive(message = "durationMinutes debe ser mayor que 0")
    Integer durationMinutes,

    @NotNull(message = "modality es requerido")
    Modality modality,

    @Size(min = 3, max = 255, message = "topic debe tener entre 3 y 255 caracteres")
    String topic,
    
    @Size(min = 5, max = 1000, message = "notes debe tener entre 5 y 1000 caracteres")
    String notes
) {
}
