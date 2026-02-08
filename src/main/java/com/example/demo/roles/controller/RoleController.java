package com.example.demo.roles.controller;

import com.example.demo.roles.dto.RoleResponseDto;
import com.example.demo.roles.service.RoleService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> listAll() {
        return ResponseEntity.ok(roleService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }
}
