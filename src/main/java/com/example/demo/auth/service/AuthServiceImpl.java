package com.example.demo.auth.service;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.auth.dto.RegisterRequestDto;
import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.roles.repository.RoleRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, 
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterRequestDto request) {
        // Validar que las contraseñas coincidan
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Validar que el username no exista
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        // Validar que el email no exista
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByName(RoleName.USER)
            .orElseThrow(() -> new IllegalStateException("Rol USER no encontrado"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        // Buscar usuario por username o email
        User user = userRepository.findByUsername(request.getIdentifier())
            .or(() -> userRepository.findByEmail(request.getIdentifier()))
            .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña inválidos"));

        // Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Usuario o contraseña inválidos");
        }

        // Obtener roles
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());

        // Generar JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        // Retornar respuesta
        return new LoginResponseDto(token, user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
