package com.example.demo.asesorias.dto;

import java.util.List;

public record AsesoriaMyRequestsResponseDto(
    List<AsesoriaResponseDto> sent,
    List<AsesoriaResponseDto> received
) {
}
