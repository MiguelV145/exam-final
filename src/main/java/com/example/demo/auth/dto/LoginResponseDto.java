package com.example.demo.auth.dto;

import com.example.demo.users.dto.UserResponseDto;

public record LoginResponseDto(
    String token,
    String tokenType,
    UserResponseDto user
) {
}
