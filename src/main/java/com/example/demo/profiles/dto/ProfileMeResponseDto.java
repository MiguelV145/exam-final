package com.example.demo.profiles.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO combinado para responder GET /api/profile/me
 * Incluye datos del Profile + skills del Portfolio de forma integrada.
 * Ejemplo:
 * {
 *   "displayName": "Juan Pérez",
 *   "photoURL": "https://...",
 *   "specialty": "Backend Java",
 *   "description": "Especialista en Spring Boot",
 *   "contactEmail": "juan@example.com",
 *   "skills": ["Java", "Spring Boot", "PostgreSQL"]
 * }
 */
public record ProfileMeResponseDto(
    String displayName,
    @JsonProperty("photoURL")
    @JsonAlias({"photoUrl"})
    String photoUrl,
    String specialty,
    String description,
    String contactEmail,
    List<String> skills
) {
}
