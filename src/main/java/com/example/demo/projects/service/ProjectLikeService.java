package com.example.demo.projects.service;

import com.example.demo.projects.entity.Project;
import com.example.demo.projects.entity.ProjectLike;
import com.example.demo.projects.repository.ProjectLikeRepository;
import com.example.demo.projects.repository.ProjectRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestionar los likes de proyectos.
 * Maneja:
 * - Toggle like/unlike
 * - Obtener información de likes de un proyecto
 */
@Service
public class ProjectLikeService {
    private final ProjectLikeRepository projectLikeRepository;
    private final ProjectRepository projectRepository;
    private final SecurityUtils securityUtils;

    public ProjectLikeService(ProjectLikeRepository projectLikeRepository,
                             ProjectRepository projectRepository,
                             SecurityUtils securityUtils) {
        this.projectLikeRepository = projectLikeRepository;
        this.projectRepository = projectRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Toggle (like/unlike) de un proyecto por el usuario actual.
     * Si el usuario no ha hecho like, lo crea.
     * Si ya lo hizo, lo elimina.
     * 
     * @param projectId el ID del proyecto
     * @return true si el like fue creado, false si fue eliminado
     * @throws ResourceNotFoundException si el proyecto no existe
     */
    @Transactional
    public boolean toggleLike(Long projectId) {
        // Verificar que el proyecto existe
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        
        // Obtener usuario actual (debe estar autenticado)
        User currentUser = securityUtils.getCurrentUser();
        
        // Verificar si ya hay like
        if (projectLikeRepository.existsByProjectIdAndUserId(projectId, currentUser.getId())) {
            // Eliminar like
            projectLikeRepository.deleteByProjectIdAndUserId(projectId, currentUser.getId());
            return false; // Unlike
        } else {
            // Crear like
            ProjectLike projectLike = new ProjectLike(currentUser, project);
            projectLikeRepository.save(projectLike);
            return true; // Like
        }
    }

    /**
     * Obtiene el total de likes para un proyecto.
     * 
     * @param projectId el ID del proyecto
     * @return el número de likes
     */
    public long getLikeCount(Long projectId) {
        return projectLikeRepository.countByProjectId(projectId);
    }

    /**
     * Verifica si el usuario actual ha hecho like a un proyecto.
     * 
     * @param projectId el ID del proyecto
     * @return true si el usuario ha hecho like, false en caso contrario
     */
    public boolean hasUserLiked(Long projectId) {
        try {
            User currentUser = securityUtils.getCurrentUser();
            return projectLikeRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        } catch (Exception e) {
            // Si no hay usuario autenticado, retornar false
            return false;
        }
    }

    /**
     * Crea un like para un proyecto por el usuario actual.
     * Incrementa el contador de likes del proyecto de forma segura.
     * 
     * @param projectId el ID del proyecto
     * @throws ResourceNotFoundException si el proyecto no existe
     */
    @Transactional
    public void like(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        
        User currentUser = securityUtils.getCurrentUser();
        
        // Crear like si no existe
        if (!projectLikeRepository.existsByProjectIdAndUserId(projectId, currentUser.getId())) {
            ProjectLike projectLike = new ProjectLike(currentUser, project);
            projectLikeRepository.save(projectLike);
            
            // Incrementar contador de likes
            int currentCount = project.getLikeCount() != null ? project.getLikeCount() : 0;
            project.setLikeCount(currentCount + 1);
            projectRepository.save(project);
        }
    }

    /**
     * Elimina un like para un proyecto por el usuario actual.
     * Decrementa el contador de likes del proyecto de forma segura (no negativo).
     * 
     * @param projectId el ID del proyecto
     * @throws ResourceNotFoundException si el proyecto no existe
     */
    @Transactional
    public void unlike(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        
        User currentUser = securityUtils.getCurrentUser();
        
        // Eliminar like si existe
        if (projectLikeRepository.existsByProjectIdAndUserId(projectId, currentUser.getId())) {
            projectLikeRepository.deleteByProjectIdAndUserId(projectId, currentUser.getId());
            
            // Decrementar contador de likes (sin ir por debajo de 0)
            int currentCount = project.getLikeCount() != null ? project.getLikeCount() : 0;
            project.setLikeCount(Math.max(0, currentCount - 1));
            projectRepository.save(project);
        }
    }
}