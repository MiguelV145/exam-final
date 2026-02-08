package com.example.demo.asesorias.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAsesoriaDto(
    @NotNull Long programmerId,
    @NotNull Long clientId,
    @NotNull LocalDate date,
    @NotNull LocalTime time,
    String comment
) {
}
