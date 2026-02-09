package com.example.demo.roles.repository;

import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Buscar rol por ID (heredado de JpaRepository)
    Optional<Role> findById(Long id);
    
    // Buscar rol por nombre
    Optional<Role> findByName(RoleName name);
}
