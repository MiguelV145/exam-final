package com.example.demo.projects.controller;

import com.example.demo.projects.dto.CreateProjectDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.dto.UpdateProjectDto;
import com.example.demo.projects.service.ProjectService;
import com.example.demo.projects.service.ProjectLikeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectLikeService projectLikeService;

    public ProjectController(ProjectService projectService, ProjectLikeService projectLikeService) {
        this.projectService = projectService;
        this.projectLikeService = projectLikeService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProjectResponseDto>> listPublic() {
        return ResponseEntity.ok(projectService.listAll());
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> listAll(@RequestParam(required = false) Long portfolioId) {
        if (portfolioId != null) {
            return ResponseEntity.ok(projectService.listByPortfolio(portfolioId));
        }
        return ResponseEntity.ok(projectService.listAll());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PROGRAMADOR')")
    public ResponseEntity<List<ProjectResponseDto>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<ProjectResponseDto> create(@Valid @RequestBody CreateProjectDto request) {
        return ResponseEntity.ok(projectService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<ProjectResponseDto> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateProjectDto request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<ProjectResponseDto> patch(@PathVariable("id") Long id, @Valid @RequestBody UpdateProjectDto request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAMADOR')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/projects/{id}/like
     * Crea un like en un proyecto (authenticated users)
     * Respuesta: {"liked": true}
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeResponse> like(@PathVariable("id") Long id) {
        projectLikeService.like(id);
        return ResponseEntity.ok(new LikeResponse(true));
    }

    /**
     * DELETE /api/projects/{id}/like
     * Elimina un like en un proyecto (authenticated users)
     * Respuesta (204 No Content)
     */
    @DeleteMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlike(@PathVariable("id") Long id) {
        projectLikeService.unlike(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/projects/{id}/like
     * Verifica si el usuario actual hizo like a un proyecto
     * Respuesta: {"liked": true/false}
     * Accesible para usuarios autenticados y no autenticados
     */
    @GetMapping("/{id}/like")
    public ResponseEntity<LikeResponse> isLikedByUser(@PathVariable("id") Long id) {
        boolean liked = projectLikeService.hasUserLiked(id);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    /**
     * DTO simple para la respuesta del toggle like.
     */
    public record LikeResponse(Boolean liked) {
    }
}