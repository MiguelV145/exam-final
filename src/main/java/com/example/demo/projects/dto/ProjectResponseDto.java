package com.example.demo.projects.dto;

import com.example.demo.projects.entity.ProjectCategory;
import java.util.List;

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
    Integer likeCount
) {
}
