package com.example.demo.projects.dto;

import com.example.demo.projects.entity.ProjectCategory;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateProjectDto(
    Long portfolioId,
    @NotBlank String title,
    @NotBlank String description,
    ProjectCategory category,
    String role,
    List<String> technologies,
    String repoUrl,
    String demoUrl,
    String imageUrl
) {
}
