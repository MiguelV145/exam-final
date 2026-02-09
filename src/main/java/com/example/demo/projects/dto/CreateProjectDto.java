package com.example.demo.projects.dto;

import com.example.demo.projects.entity.ProjectCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateProjectDto(
    Long portfolioId,
    
    @NotBlank(message = "Title no puede estar vacío")
    @Size(min = 3, max = 150, message = "Title debe tener entre 3 y 150 caracteres")
    String title,
    
    @NotBlank(message = "Description no puede estar vacío")
    @Size(min = 10, max = 2000, message = "Description debe tener entre 10 y 2000 caracteres")
    String description,
    
    @NotNull(message = "Category es requerido")
    ProjectCategory category,
    
    String role,
    List<String> technologies,
    String repoUrl,
    String demoUrl,
    String imageUrl
) {
}
