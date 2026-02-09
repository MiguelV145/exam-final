package com.example.demo.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public record CreatePortfolioDto(
    @NotBlank(message = "Availability days no puede estar vacío")
    String availabilityDays,
    
    @NotNull(message = "Availability start es requerido")
    LocalTime availabilityStart,
    
    @NotNull(message = "Availability end es requerido")
    LocalTime availabilityEnd,
    
    List<String> skills
) {
}
