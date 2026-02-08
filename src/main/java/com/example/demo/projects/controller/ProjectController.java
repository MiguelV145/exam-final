package com.example.demo.projects.controller;

import com.example.demo.projects.dto.CreateProjectDto;
import com.example.demo.projects.dto.ProjectResponseDto;
import com.example.demo.projects.dto.UpdateProjectDto;
import com.example.demo.projects.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> listAll(@RequestParam(required = false) Long portfolioId) {
        if (portfolioId != null) {
            return ResponseEntity.ok(projectService.listByPortfolio(portfolioId));
        }
        return ResponseEntity.ok(projectService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> create(@Valid @RequestBody CreateProjectDto request) {
        return ResponseEntity.ok(projectService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateProjectDto request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
