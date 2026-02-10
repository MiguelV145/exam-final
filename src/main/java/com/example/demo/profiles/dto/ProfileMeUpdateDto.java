package com.example.demo.profiles.dto;

import java.util.List;

/**
 * DTO para actualizar GET /api/profile/me
 * Incluye datos del Profile + skills del Portfolio.
 * Todos los campos son opcionales excepto que displayName nunca será null.
 * Ejemplo:
 * {
 *   "displayName": "Juan Pérez",
 *   "photoUrl": "https://...",
 *   "specialty": "Backend Java",
 *   "description": "Especialista en Spring Boot",
 *   "contactEmail": "juan@example.com",
 *   "skills": ["Java", "Spring Boot", "PostgreSQL"]
 * }
 */
public record ProfileMeUpdateDto(
    String displayName,
    String photoUrl,
    String specialty,
    String description,
    String contactEmail,
    List<String> skills
) {
}
