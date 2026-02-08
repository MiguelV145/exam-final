package com.example.demo.users.service;

import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.roles.repository.RoleRepository;
import com.example.demo.shared.exception.BadRequestException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.dto.CreateUserDto;
import com.example.demo.users.dto.UpdateUserDto;
import com.example.demo.users.dto.UserResponseDto;
import com.example.demo.users.entity.User;
import com.example.demo.users.mapper.UserMapper;
import com.example.demo.users.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponseDto> listAll() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Override
    public UserResponseDto getById(Long id) {
        return userRepository.findById(id)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserResponseDto create(CreateUserDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(request.enabled() == null || request.enabled());
        user.setRoles(resolveRoles(request.roleNames()));
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponseDto update(Long id, UpdateUserDto request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        if (request.roleNames() != null) {
            user.setRoles(resolveRoles(request.roleNames()));
        }
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new BadRequestException("Default role USER not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(defaultRole);
            return roles;
        }
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
            roles.add(role);
        }
        return roles;
    }
}
