package com.example.demo.users.mapper;

import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.users.dto.UserResponseDto;
import com.example.demo.users.entity.User;
import java.util.Set;
import java.util.stream.Collectors;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserResponseDto toResponse(User user) {
        Set<RoleName> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        Long profileId = user.getProfile() == null ? null : user.getProfile().getId();
        Long portfolioId = user.getPortfolio() == null ? null : user.getPortfolio().getId();
        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.isEnabled(),
            roles,
            profileId,
            portfolioId
        );
    }
}
