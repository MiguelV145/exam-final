package com.example.demo.portfolio.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public record PortfolioRequest(
    @NotNull Long ownerId,
    String availabilityDays,
    LocalTime availabilityStart,
    LocalTime availabilityEnd,
    List<String> skills
) {
}
