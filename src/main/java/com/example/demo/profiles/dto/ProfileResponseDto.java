package com.example.demo.profiles.dto;

import java.util.List;

public record ProfileResponseDto(
    Long id,
    Long userId,
    String displayName,
    String photoUrl,
    String specialty,
    String description,
    String contactEmail,
    List<String> skills
) {
}
