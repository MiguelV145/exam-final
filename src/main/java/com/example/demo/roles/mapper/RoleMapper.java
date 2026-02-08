package com.example.demo.roles.mapper;

import com.example.demo.roles.dto.RoleResponseDto;
import com.example.demo.roles.entity.Role;

public final class RoleMapper {
    private RoleMapper() {
    }

    public static RoleResponseDto toResponse(Role role) {
        return new RoleResponseDto(role.getId(), role.getName());
    }
}
