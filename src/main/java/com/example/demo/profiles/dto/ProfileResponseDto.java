package com.example.demo.profiles.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProfileResponseDto(
    Long id,
    Long userId,
    String displayName,
    @JsonProperty("photoURL")
    @JsonAlias({"photoUrl"})
    String photoUrl,
    String specialty,
    String description,
    String contactEmail,
    List<String> skills
) {
}
