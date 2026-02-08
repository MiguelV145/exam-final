package com.example.demo.roles.repository;

import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
