package com.example.demo.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.util.List;

public record UpdatePortfolioDto(
    @NotBlank(message = "Availability days no puede estar vacío")
    String availabilityDays,
    
    LocalTime availabilityStart,
    LocalTime availabilityEnd,
    List<String> skills
) {
}
