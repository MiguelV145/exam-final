package com.example.demo.portfolio.dto;

import java.time.LocalTime;
import java.util.List;

public record PortfolioResponseDto(
    Long id,
    Long userId,
    String availabilityDays,
    LocalTime availabilityStart,
    LocalTime availabilityEnd,
    List<String> skills
) {
}
