package com.example.demo.profiles.dto;

public record ProfileResponseDto(
    Long id,
    Long userId,
    String displayName,
    String photoUrl,
    String specialty,
    String description,
    String contactEmail
) {
}
