package com.example.demo.projects.service;

import com.example.demo.portfolio.repository.PortfolioRepository;
import com.example.demo.projects.dto.CreateProjectDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.dto.UpdateProjectDto;
import com.example.demo.projects.entity.Project;
import com.example.demo.projects.mapper.ProjectMapper;
import com.example.demo.projects.repository.ProjectRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.shared.exception.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final SecurityUtils securityUtils;
    private final ProjectLikeService projectLikeService;

    public ProjectServiceImpl(ProjectRepository projectRepository, 
                            PortfolioRepository portfolioRepository,
                            SecurityUtils securityUtils,
                            ProjectLikeService projectLikeService) {
        this.projectRepository = projectRepository;
        this.portfolioRepository = portfolioRepository;
        this.securityUtils = securityUtils;
        this.projectLikeService = projectLikeService;
    }

    @Override
    public List<ProjectResponseDto> listAll() {
        return projectRepository.findAll().stream()
            .map(project -> ProjectMapper.toResponse(project, projectLikeService.hasUserLiked(project.getId())))
            .toList();
    }

    @Override
    public List<ProjectResponseDto> listByPortfolio(Long portfolioId) {
        return projectRepository.findByPortfolioId(portfolioId).stream()
            .map(project -> ProjectMapper.toResponse(project, projectLikeService.hasUserLiked(project.getId())))
            .toList();
    }

    @Override
    public List<ProjectResponseDto> getMyProjects() {
        Long currentUserId = securityUtils.getCurrentUserId();
        return projectRepository.findByPortfolioOwnerId(currentUserId).stream()
            .map(project -> ProjectMapper.toResponse(project, projectLikeService.hasUserLiked(project.getId())))
            .toList();
    }

    @Override
    public ProjectResponseDto getById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        return ProjectMapper.toResponse(project, projectLikeService.hasUserLiked(id));
    }

    @Override
    @Transactional
    public ProjectResponseDto create(CreateProjectDto request) {
        Project project = ProjectMapper.toEntity(request);
        
        Long portfolioId = request.portfolioId();
        
        // Si es PROGRAMADOR (no ADMIN), usa su propio portfolio e ignora el portfolioId del DTO
        if (securityUtils.isProgrammer() && !securityUtils.isAdmin()) {
            Long currentUserId = securityUtils.getCurrentUserId();
            var portfolio = portfolioRepository.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado para el programador actual"));
            project.setPortfolio(portfolio);
        } else if (securityUtils.isAdmin() && portfolioId != null) {
            // ADMIN puede especificar portfolioId
            var portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portafolio no encontrado"));
            project.setPortfolio(portfolio);
        } else {
            throw new ResourceNotFoundException("Portafolio no especificado o no encontrado");
        }
        
        Project savedProject = projectRepository.save(project);
        return ProjectMapper.toResponse(savedProject, false);
    }

    @Override
    @Transactional
    public ProjectResponseDto update(Long id, UpdateProjectDto request) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        
        // ADMIN puede editar cualquiera, PROGRAMADOR solo si es su portfolio
        if (!securityUtils.isAdmin() && 
            (project.getPortfolio() == null || !project.getPortfolio().getOwner().getId().equals(securityUtils.getCurrentUserId()))) {
            throw new ForbiddenException("No tienes permiso para actualizar este proyecto");
        }
        
        ProjectMapper.updateEntity(request, project);
        Project updatedProject = projectRepository.save(project);
        return ProjectMapper.toResponse(updatedProject, projectLikeService.hasUserLiked(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        
        // ADMIN puede eliminar cualquiera, PROGRAMADOR solo si es su portfolio
        if (!securityUtils.isAdmin() && 
            (project.getPortfolio() == null || !project.getPortfolio().getOwner().getId().equals(securityUtils.getCurrentUserId()))) {
            throw new ForbiddenException("No tienes permiso para eliminar este proyecto");
        }
        
        projectRepository.deleteById(id);
    }
}
