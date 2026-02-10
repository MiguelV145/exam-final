package com.example.demo.security;

import com.example.demo.roles.entity.RoleName;
import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilidad para obtener información del usuario autenticado desde el SecurityContext.
 * Centraliza la lógica de seguridad y ownership.
 */
@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Obtiene el usuario actualmente autenticado.
     * 
     * @return el User de la BD
     * @throws IllegalArgumentException si no hay usuario autenticado
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("No user is currently authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado.
     * 
     * @return el ID del User
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    /**
     * Verifica si el usuario actual tiene un rol específico.
     * 
     * @param roleName el rol a verificar
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean hasRole(RoleName roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String rolePrefix = "ROLE_" + roleName.name();
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals(rolePrefix));
    }

    /**
     * Verifica si el usuario actual es ADMIN.
     * 
     * @return true si es ADMIN
     */
    public boolean isAdmin() {
        return hasRole(RoleName.ADMIN);
    }

    /**
     * Verifica si el usuario actual es PROGRAMADOR.
     * 
     * @return true si es PROGRAMADOR
     */
    public boolean isProgrammer() {
        return hasRole(RoleName.PROGRAMADOR);
    }

    /**
     * Verifica si el usuario actual es USER.
     * 
     * @return true si es USER
     */
    public boolean isUser() {
        return hasRole(RoleName.USER);
    }
}
