package com.example.demo.projects.service;

import com.example.demo.portfolio.repository.PortfolioRepository;
import com.example.demo.projects.dto.CreateProjectDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.dto.UpdateProjectDto;
import com.example.demo.projects.entity.Project;
import com.example.demo.projects.mapper.ProjectMapper;
import com.example.demo.projects.repository.ProjectRepository;
import com.example.demo.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, PortfolioRepository portfolioRepository) {
        this.projectRepository = projectRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public List<ProjectResponseDto> listAll() {
        return projectRepository.findAll().stream().map(ProjectMapper::toResponse).toList();
    }

    @Override
    public List<ProjectResponseDto> listByPortfolio(Long portfolioId) {
        return projectRepository.findByPortfolioId(portfolioId).stream().map(ProjectMapper::toResponse).toList();
    }

    @Override
    public ProjectResponseDto getById(Long id) {
        return projectRepository.findById(id)
            .map(ProjectMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @Override
    public ProjectResponseDto create(CreateProjectDto request) {
        Project project = ProjectMapper.toEntity(request);
        if (request.portfolioId() != null) {
            project.setPortfolio(portfolioRepository.findById(request.portfolioId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found")));
        }
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDto update(Long id, UpdateProjectDto request) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        ProjectMapper.updateEntity(request, project);
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(id);
    }
}

             