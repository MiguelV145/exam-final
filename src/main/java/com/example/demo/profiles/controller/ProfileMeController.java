package com.example.demo.profiles.controller;

import com.example.demo.profiles.dto.ProfileMeResponseDto;
import com.example.demo.profiles.dto.ProfileMeUpdateDto;
import com.example.demo.profiles.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller para el perfil del usuario autenticado.
 * Proporciona endpoints compatibles con el frontend Angular.
 * Todos los endpoints requieren JWT authentication.
 *
 * CURL Examples:
 *   GET con JWT:
 *     curl -H "Authorization: Bearer {token}" http://localhost:8080/api/profile/me
 *
 *   PUT para actualizar:
 *     curl -X PUT -H "Authorization: Bearer {token}" \
 *          -H "Content-Type: application/json" \
 *          -d '{"displayName":"Juan","specialty":"Backend","skills":["Java","Spring"]}' \
 *          http://localhost:8080/api/profile/me
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileMeController {
    
    private final ProfileService profileService;

    public ProfileMeController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * GET /api/profile/me
     * Obtiene el perfil del usuario autenticado junto con sus skills.
     * Si el usuario no tiene perfil, se crea uno automáticamente.
     * Si el usuario no tiene portfolio, se crea uno automáticamente.
     *
     * @return ProfileMeResponseDto con datos combinados de Profile + Portfolio.skills
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileMeResponseDto> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfileCombined());
    }

    /**
     * PUT /api/profile/me
     * Actualiza el perfil del usuario autenticado y sus skills en una sola transacción.
     * Si el usuario no tiene perfil, se crea uno automáticamente.
     * Si el usuario no tiene portfolio, se crea uno automáticamente.
     * displayName nunca será null (usa username como fallback).
     *
     * @param request ProfileMeUpdateDto con datos a actualizar
     * @return ProfileMeResponseDto con los datos actualizados
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileMeResponseDto> updateMyProfile(@Valid @RequestBody ProfileMeUpdateDto request) {
        return ResponseEntity.ok(profileService.updateMyProfileCombined(request));
    }
}
