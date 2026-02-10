package com.example.demo.profiles.service;

import com.example.demo.profiles.dto.ProfileMeResponseDto;
import com.example.demo.profiles.dto.ProfileMeUpdateDto;
import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.profiles.dto.UpdateProfileDto;

public interface ProfileService {
    ProfileResponseDto getMyProfile();
    ProfileResponseDto updateMyProfile(UpdateProfileDto request);
    ProfileResponseDto getProfileByUserId(Long userId);
    
    // Métodos para el endpoint /api/profile/me (singular)
    ProfileMeResponseDto getMyProfileCombined();
    ProfileMeResponseDto updateMyProfileCombined(ProfileMeUpdateDto request);
}

