package com.example.demo.asesorias.dto;

import com.example.demo.asesorias.entity.AsesoriaStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record AsesoriaResponseDto(
    Long id,
    LocalDate date,
    LocalTime time,
    String comment,
    AsesoriaStatus status,
    String responseMsg,
    Long programmerId,
    Long clientId
) {
}
