package com.example.demo.roles.service;

import com.example.demo.roles.dto.RoleResponseDto;
import com.example.demo.roles.mapper.RoleMapper;
import com.example.demo.roles.repository.RoleRepository;
import com.example.demo.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponseDto> listAll() {
        return roleRepository.findAll().stream().map(RoleMapper::toResponse).toList();
    }

    @Override
    public RoleResponseDto getById(Long id) {
        return roleRepository.findById(id)
            .map(RoleMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }
}
