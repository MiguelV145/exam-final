package com.example.demo.profiles.service;

import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.profiles.dto.UpdateProfileDto;
import com.example.demo.profiles.entity.Profile;
import com.example.demo.profiles.mapper.ProfileMapper;
import com.example.demo.profiles.repository.ProfileRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.shared.exception.ForbiddenException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {
    
    private final ProfileRepository profileRepository;
    private final SecurityUtils securityUtils;

    public ProfileServiceImpl(ProfileRepository profileRepository, SecurityUtils securityUtils) {
        this.profileRepository = profileRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public ProfileResponseDto getMyProfile() {
        User currentUser = securityUtils.getCurrentUser();
        Long currentUserId = currentUser.getId();
        
        // Si el usuario no tiene profile, crear uno vacío y guardarlo
        Profile profile = profileRepository.findByUserId(currentUserId)
            .orElseGet(() -> {
                Profile newProfile = new Profile();
                newProfile.setUser(currentUser);
                newProfile.setDisplayName(currentUser.getUsername());
                return profileRepository.save(newProfile);
            });
        
        return ProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateMyProfile(UpdateProfileDto request) {
        User currentUser = securityUtils.getCurrentUser();
        Long currentUserId = currentUser.getId();
        
        // Obtener el profile del usuario actual, si no existe, crear uno
        Profile profile = profileRepository.findByUserId(currentUserId)
            .orElseGet(() -> {
                Profile newProfile = new Profile();
                newProfile.setUser(currentUser);
                newProfile.setDisplayName(currentUser.getUsername());
                return newProfile;
            });
        
        // Actualizar los campos permitidos
        ProfileMapper.updateEntity(request, profile);
        
        // Asegurar que displayName nunca sea null o blank (usar username como fallback)
        if (profile.getDisplayName() == null || profile.getDisplayName().isBlank()) {
            profile.setDisplayName(currentUser.getUsername());
        }
        
        return ProfileMapper.toResponse(profileRepository.save(profile));
    }

    @Override
    public ProfileResponseDto getProfileByUserId(Long userId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Solo ADMIN puede ver los profiles de otros usuarios
        if (!securityUtils.isAdmin() && !userId.equals(currentUserId)) {
            throw new ForbiddenException("You do not have permission to view this profile");
        }
        
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user"));
        
        return ProfileMapper.toResponse(profile);
    }
}
