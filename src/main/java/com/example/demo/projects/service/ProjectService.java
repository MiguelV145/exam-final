package com.example.demo.projects.service;

import com.example.demo.projects.dto.CreateProjectDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.dto.UpdateProjectDto;
import java.util.List;

public interface ProjectService {
    List<ProjectResponseDto> listAll();
    List<ProjectResponseDto> listByPortfolio(Long portfolioId);
    ProjectResponseDto getById(Long id);
    ProjectResponseDto create(CreateProjectDto request);
    ProjectResponseDto update(Long id, UpdateProjectDto request);
    void delete(Long id);
}
