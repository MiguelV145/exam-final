package com.example.demo.roles.service;

import com.example.demo.roles.dto.RoleResponseDto;
import java.util.List;

public interface RoleService {
    List<RoleResponseDto> listAll();
    RoleResponseDto getById(Long id);
}
