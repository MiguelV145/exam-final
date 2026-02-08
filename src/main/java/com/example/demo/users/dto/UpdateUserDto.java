package com.example.demo.users.dto;

import com.example.demo.roles.entity.RoleName;
import java.util.Set;

public record UpdateUserDto(
    Boolean enabled,
    Set<RoleName> roleNames
) {
}
