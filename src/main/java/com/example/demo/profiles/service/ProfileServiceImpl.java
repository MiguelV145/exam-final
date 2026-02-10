package com.example.demo.profiles.service;

import com.example.demo.portfolio.entity.Portfolio;
import com.example.demo.portfolio.repository.PortfolioRepository;
import com.example.demo.profiles.dto.ProfileMeResponseDto;
import com.example.demo.profiles.dto.ProfileMeUpdateDto;
import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.profiles.dto.UpdateProfileDto;
import com.example.demo.profiles.entity.Profile;
import com.example.demo.profiles.mapper.ProfileMapper;
import com.example.demo.profiles.repository.ProfileRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.shared.exception.ForbiddenException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {
    
    private final ProfileRepository profileRepository;
    private final PortfolioRepository portfolioRepository;
    private final SecurityUtils securityUtils;

    public ProfileServiceImpl(ProfileRepository profileRepository, 
                            PortfolioRepository portfolioRepository,
                            SecurityUtils securityUtils) {
        this.profileRepository = profileRepository;
        this.portfolioRepository = portfolioRepository;
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
            .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para el usuario"));
        
        return ProfileMapper.toResponse(profile);
    }

    /**
     * Obtiene el perfil combinado (Profile + skills) del usuario autenticado.
     * Si el usuario no tiene perfil, se crea automáticamente.
     */
    @Override
    public ProfileMeResponseDto getMyProfileCombined() {
        User currentUser = securityUtils.getCurrentUser();
        Long currentUserId = currentUser.getId();
        
        // Obtener o crear el perfil del usuario
        Profile profile = profileRepository.findByUserId(currentUserId)
            .orElseGet(() -> {
                Profile newProfile = new Profile();
                newProfile.setUser(currentUser);
                newProfile.setDisplayName(currentUser.getUsername());
                newProfile.setSkills(new ArrayList<>());
                return profileRepository.save(newProfile);
            });
        
        // Retornar DTO combinado
        return new ProfileMeResponseDto(
            profile.getDisplayName(),
            profile.getPhotoUrl(),
            profile.getSpecialty(),
            profile.getDescription(),
            profile.getContactEmail(),
            profile.getSkills()
        );
    }

    /**
     * Actualiza el perfil combinado (Profile + skills) del usuario autenticado.
     * Si el usuario no tiene perfil, se crea automáticamente.
     * Todos los campos del DTO se aplican, pero displayName nunca será null.
     */
    @Override
    @Transactional
    public ProfileMeResponseDto updateMyProfileCombined(ProfileMeUpdateDto request) {
        User currentUser = securityUtils.getCurrentUser();
        Long currentUserId = currentUser.getId();
        
        // Obtener o crear el perfil del usuario
        Profile profile = profileRepository.findByUserId(currentUserId)
            .orElseGet(() -> {
                Profile newProfile = new Profile();
                newProfile.setUser(currentUser);
                newProfile.setDisplayName(currentUser.getUsername());
                newProfile.setSkills(new ArrayList<>());
                return newProfile;
            });
        
        // Actualizar campos del perfil si se proporcionan
        if (request.displayName() != null && !request.displayName().isBlank()) {
            profile.setDisplayName(request.displayName());
        } else {
            profile.setDisplayName(currentUser.getUsername()); // Fallback
        }
        if (request.photoUrl() != null) {
            profile.setPhotoUrl(request.photoUrl());
        }
        if (request.specialty() != null) {
            profile.setSpecialty(request.specialty());
        }
        if (request.description() != null) {
            profile.setDescription(request.description());
        }
        if (request.contactEmail() != null) {
            profile.setContactEmail(request.contactEmail());
        }
        
        // Actualizar skills si se proporcionan
        if (request.skills() != null) {
            profile.setSkills(request.skills());
        }
        
        // Guardar el perfil actualizado
        profileRepository.save(profile);
        
        // Retornar DTO combinado actualizado
        return new ProfileMeResponseDto(
            profile.getDisplayName(),
            profile.getPhotoUrl(),
            profile.getSpecialty(),
            profile.getDescription(),
            profile.getContactEmail(),
            profile.getSkills()
        );
    }
}
