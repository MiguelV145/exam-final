package com.example.demo.profiles.mapper;

import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.profiles.dto.UpdateProfileDto;
import com.example.demo.profiles.entity.Profile;

public final class ProfileMapper {
    private ProfileMapper() {
    }

    public static void updateEntity(UpdateProfileDto request, Profile profile) {
        if (request.displayName() != null) {
            profile.setDisplayName(request.displayName());
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
    }

    public static ProfileResponseDto toResponse(Profile profile) {
        Long userId = profile.getUser() == null ? null : profile.getUser().getId();
        return new ProfileResponseDto(
            profile.getId(),
            userId,
            profile.getDisplayName(),
            profile.getPhotoUrl(),
            profile.getSpecialty(),
            profile.getDescription(),
            profile.getContactEmail()
        );
    }
}
