package com.example.demo.profiles.service;

import com.example.demo.profiles.dto.ProfileResponseDto;
import com.example.demo.profiles.dto.UpdateProfileDto;

public interface ProfileService {
    ProfileResponseDto getMyProfile();
    ProfileResponseDto updateMyProfile(UpdateProfileDto request);
    ProfileResponseDto getProfileByUserId(Long userId);
}

