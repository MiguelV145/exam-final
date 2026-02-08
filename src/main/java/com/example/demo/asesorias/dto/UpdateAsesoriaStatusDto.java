package com.example.demo.asesorias.dto;

import com.example.demo.asesorias.entity.AsesoriaStatus;

public record UpdateAsesoriaStatusDto(
    AsesoriaStatus status,
    String responseMsg
) {
}
