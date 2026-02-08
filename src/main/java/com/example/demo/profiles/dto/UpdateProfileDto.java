package com.example.demo.profiles.dto;

public record UpdateProfileDto(
    String displayName,
    String photoUrl,
    String specialty,
    String description,
    String contactEmail
) {
}
