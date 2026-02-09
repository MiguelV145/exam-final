package com.example.demo.projects.controller;

import com.example.demo.projects.service.ProjectLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestionar los likes de proyectos.
 * Todos los endpoints requieren autenticación.
 */
@RestController
@RequestMapping("/api/projects")
@PreAuthorize("hasAnyRole('USER', 'PROGRAMADOR', 'ADMIN')")
public class ProjectLikeController {

    private final ProjectLikeService projectLikeService;

    public ProjectLikeController(ProjectLikeService projectLikeService) {
        this.projectLikeService = projectLikeService;
    }

    /**
     * Toggle like/unlike en un proyecto.
     * Si el usuario ya dio like, lo elimina. Si no, lo crea.
     * 
     * @param projectId el ID del proyecto
     * @return JSON con el estado del like y el conteo total
     */
    @PostMapping("/{projectId}/like/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long projectId) {
        boolean liked = projectLikeService.toggleLike(projectId);
        long likeCount = projectLikeService.getLikeCount(projectId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si el usuario actual ha dado like al proyecto.
     * 
     * @param projectId el ID del proyecto
     * @return JSON con el estado del like
     */
    @GetMapping("/{projectId}/like/me")
    public ResponseEntity<Map<String, Boolean>> hasUserLiked(@PathVariable Long projectId) {
        boolean hasLiked = projectLikeService.hasUserLiked(projectId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", hasLiked);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el conteo total de likes de un proyecto.
     * 
     * @param projectId el ID del proyecto
     * @return JSON con el conteo de likes
     */
    @GetMapping("/{projectId}/like/count")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable Long projectId) {
        long likeCount = projectLikeService.getLikeCount(projectId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }
}
