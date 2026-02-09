package com.example.demo.projects.dto;

import com.example.demo.projects.entity.ProjectCategory;
import java.util.List;

/**
 * DTO para la respuesta de un proyecto.
 * Incluye:
 * - likeCount: número total de likes del proyecto
 * - likedByMe: booleano indicando si el usuario actual (si está autenticado) ha hecho like
 */
public record ProjectResponseDto(
    Long id,
    Long portfolioId,
    String title,
    String description,
    ProjectCategory category,
    String role,
    List<String> technologies,
    String repoUrl,
    String demoUrl,
    String imageUrl,
    Integer likeCount,
    Boolean likedByMe
) {
}
