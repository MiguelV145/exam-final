package com.example.demo.profiles.controller;

import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.profiles.dto.UpdateProfileDto;
import com.example.demo.profiles.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/profile", "/api/profiles"})
public class ProfileController {
    
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponseDto> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponseDto> updateMyProfile(@Valid @RequestBody UpdateProfileDto request) {
        return ResponseEntity.ok(profileService.updateMyProfile(request));
    }

    /**
     * GET /api/profiles/{userId}
     * Obtiene el perfil de un usuario específico.
     * Solo ADMIN puede ver cualquier perfil, o el usuario puede ver el suyo.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }
}
