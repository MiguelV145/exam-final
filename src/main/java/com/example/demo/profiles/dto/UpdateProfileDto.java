package com.example.demo.profiles.dto;

import java.util.List;

public record UpdateProfileDto(
    String displayName,
    String photoUrl,
    String specialty,
    String description,
    String contactEmail,
    List<String> skills
) {
}
