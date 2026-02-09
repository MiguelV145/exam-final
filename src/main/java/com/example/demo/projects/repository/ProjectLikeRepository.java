package com.example.demo.projects.repository;

import com.example.demo.projects.entity.ProjectLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para gestionar los likes de proyectos.
 * Proporciona métodos para crear, eliminar y verificar likes persistidos en BD.
 */
@Repository
public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {

    /**
     * Verifica si un usuario ha hecho like a un proyecto específico.
     * 
     * @param projectId el ID del proyecto
     * @param userId el ID del usuario
     * @return true si existe like, false en caso contrario
     */
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    /**
     * Obtiene el like entre un usuario y un proyecto.
     * 
     * @param projectId el ID del proyecto
     * @param userId el ID del usuario
     * @return Optional con el ProjectLike si existe
     */
    Optional<ProjectLike> findByProjectIdAndUserId(Long projectId, Long userId);

    /**
     * Cuenta el total de likes para un proyecto.
     * 
     * @param projectId el ID del proyecto
     * @return el número de likes
     */
    @Query("SELECT COUNT(pl) FROM ProjectLike pl WHERE pl.project.id = :projectId")
    long countByProjectId(@Param("projectId") Long projectId);

    /**
     * Elimina el like entre un usuario y un proyecto.
     * 
     * @param projectId el ID del proyecto
     * @param userId el ID del usuario
     */
    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
