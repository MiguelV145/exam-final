package com.example.demo.asesorias.dto;

import jakarta.validation.constraints.Size;

public record CancelAsesoriaDto(
    @Size(max = 1000, message = "Response message debe tener máximo 1000 caracteres")
    String responseMsg
) {
}
