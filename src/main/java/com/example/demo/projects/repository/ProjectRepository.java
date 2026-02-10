package com.example.demo.projects.repository;

import com.example.demo.projects.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Buscar proyecto por ID (heredado de JpaRepository)
    Optional<Project> findById(Long id);
    
    // Buscar proyectos por portfolio
    List<Project> findByPortfolioId(Long portfolioId);
    
    // Buscar proyecto con fetch join para evitar lazy initialization con open-in-view=false
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.portfolio WHERE p.id = :id")
    Optional<Project> findByIdWithPortfolio(@Param("id") Long id);
    
    // Validar si un proyecto pertenece a un owner específico (a través del portfolio)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Project p WHERE p.id = :projectId AND p.portfolio.owner.id = :ownerId")
    boolean existsByIdAndPortfolioOwnerId(@Param("projectId") Long projectId, @Param("ownerId") Long ownerId);
    
    // Listar proyectos de un owner (a través de su portfolio)
    @Query("SELECT p FROM Project p WHERE p.portfolio.owner.id = :ownerId")
    List<Project> findByPortfolioOwnerId(@Param("ownerId") Long ownerId);
    
    // Contar proyectos por usuario para reportes
    @Query("SELECT p.portfolio.owner.id, p.portfolio.owner.username, COUNT(p) " +
           "FROM Project p " +
           "GROUP BY p.portfolio.owner.id, p.portfolio.owner.username " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countProjectsByUser();
}
