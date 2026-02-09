package com.example.demo.portfolio.repository;

import com.example.demo.portfolio.entity.Portfolio;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // Buscar portfolio por ID (heredado de JpaRepository)
    Optional<Portfolio> findById(Long id);
    
    // Buscar portfolio por owner ID
    Optional<Portfolio> findByOwnerId(Long ownerId);
    
    // Buscar portfolio por username del owner
    @Query("SELECT p FROM Portfolio p WHERE p.owner.username = :username")
    Optional<Portfolio> findByOwnerUsername(@Param("username") String username);
}
