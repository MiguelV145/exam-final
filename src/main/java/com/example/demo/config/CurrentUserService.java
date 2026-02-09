package com.example.demo.config;

import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import com.example.demo.shared.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene el usuario actualmente autenticado desde el SecurityContext
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No user is currently authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Verifica si el usuario actual es ADMIN
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Verifica si el usuario actual es PROGRAMADOR
     */
    public boolean isProgrammer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROGRAMADOR"));
    }

    /**
     * Verifica si el usuario actual es USER
     */
    public boolean isUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    }
}
