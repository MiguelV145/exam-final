package com.example.demo.projects.repository;

import com.example.demo.projects.entity.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByPortfolioId(Long portfolioId);
}
