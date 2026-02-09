package com.example.demo.users.service;

import com.example.demo.roles.entity.RoleName;
import com.example.demo.users.dto.CreateUserDto;
import com.example.demo.users.dto.UpdateUserDto;
import com.example.demo.users.dto.UserResponseDto;
import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserResponseDto> listAll();
    UserResponseDto getById(Long id);
    UserResponseDto create(CreateUserDto request);
    UserResponseDto update(Long id, UpdateUserDto request);
    void delete(Long id);
    UserResponseDto assignRoles(Long id, Set<RoleName> roleNames);
    UserResponseDto makeProgrammer(Long id);
}
