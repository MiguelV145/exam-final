package com.example.demo.projects.mapper;

import com.example.demo.projects.dto.CreateProjectDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.dto.UpdateProjectDto;
import com.example.demo.projects.entity.Project;

public final class ProjectMapper {
    private ProjectMapper() {
    }

    public static Project toEntity(CreateProjectDto request) {
        Project project = new Project();
        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setCategory(request.category());
        project.setRole(request.role());
        project.setTechnologies(request.technologies());
        project.setRepoUrl(request.repoUrl());
        project.setDemoUrl(request.demoUrl());
        project.setImageUrl(request.imageUrl());
        return project;
    }

    public static void updateEntity(UpdateProjectDto request, Project project) {
        if (request.title() != null) {
            project.setTitle(request.title());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }
        if (request.category() != null) {
            project.setCategory(request.category());
        }
        if (request.role() != null) {
            project.setRole(request.role());
        }
        if (request.technologies() != null) {
            project.setTechnologies(request.technologies());
        }
        if (request.repoUrl() != null) {
            project.setRepoUrl(request.repoUrl());
        }
        if (request.demoUrl() != null) {
            project.setDemoUrl(request.demoUrl());
        }
        if (request.imageUrl() != null) {
            project.setImageUrl(request.imageUrl());
        }
    }

    public static ProjectResponseDto toResponse(Project project) {
        Long portfolioId = project.getPortfolio() == null ? null : project.getPortfolio().getId();
        return new ProjectResponseDto(
            project.getId(),
            portfolioId,
            project.getTitle(),
            project.getDescription(),
            project.getCategory(),
            project.getRole(),
            project.getTechnologies(),
            project.getRepoUrl(),
            project.getDemoUrl(),
            project.getImageUrl(),
            project.getLikeCount()
        );
    }
}

