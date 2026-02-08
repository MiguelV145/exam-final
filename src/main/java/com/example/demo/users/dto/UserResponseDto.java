package com.example.demo.users.dto;

import com.example.demo.roles.entity.RoleName;
import java.util.Set;

public record UserResponseDto(
    Long id,
    String username,
    String email,
    boolean enabled,
    Set<RoleName> roles,
    Long profileId,
    Long portfolioId
) {
}
