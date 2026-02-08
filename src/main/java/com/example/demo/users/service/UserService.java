package com.example.demo.users.service;

import com.example.demo.users.dto.CreateUserDto;
import com.example.demo.users.dto.UpdateUserDto;
import com.example.demo.users.dto.UserResponseDto;
import java.util.List;

public interface UserService {
    List<UserResponseDto> listAll();
    UserResponseDto getById(Long id);
    UserResponseDto create(CreateUserDto request);
    UserResponseDto update(Long id, UpdateUserDto request);
    void delete(Long id);
}
