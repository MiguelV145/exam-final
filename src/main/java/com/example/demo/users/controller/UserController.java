package com.example.demo.users.controller;

import com.example.demo.roles.entity.RoleName;
import com.example.demo.users.dto.AssignRolesDto;
import com.example.demo.users.dto.CreateUserDto;
import com.example.demo.users.dto.UpdateUserDto;
import com.example.demo.users.dto.UserResponseDto;
import com.example.demo.users.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> listAll() {
        return ResponseEntity.ok(userService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody CreateUserDto request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateUserDto request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/users/{id}/roles
     * Asigna una lista de roles a un usuario (ADMIN only)
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody AssignRolesDto request) {
        return ResponseEntity.ok(userService.assignRoles(id, request.roleNames()));
    }

    /**
     * PUT /api/users/{id}/make-programmer
     * Asigna el rol PROGRAMADOR e crea Portfolio y Profile si no existen (ADMIN only)
     */
    @PutMapping("/{id}/make-programmer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> makeProgrammer(@PathVariable Long id) {
        return ResponseEntity.ok(userService.makeProgrammer(id));
    }
}
