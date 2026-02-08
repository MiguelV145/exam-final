package com.example.demo.projects.dto;

import com.example.demo.projects.entity.ProjectCategory;
import java.util.List;

public record UpdateProjectDto(
    String title,
    String description,
    ProjectCategory category,
    String role,
    List<String> technologies,
    String repoUrl,
    String demoUrl,
    String imageUrl
) {
}
