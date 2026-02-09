package com.example.demo.users.repository;

import com.example.demo.users.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por ID (heredado de JpaRepository)
    Optional<User> findById(Long id);

    // Buscar usuario por username
    Optional<User> findByUsername(String username);
    
    // Buscar usuario por email (usado en login)
    Optional<User> findByEmail(String email);
    
    // Verificar si username ya está registrado
    boolean existsByUsername(String username);
    
    // Verificar si email ya está registrado (usado en registro)
    boolean existsByEmail(String email);
}
