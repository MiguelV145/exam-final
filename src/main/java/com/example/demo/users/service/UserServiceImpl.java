package com.example.demo.users.service;

import com.example.demo.portfolio.entity.Portfolio;
import com.example.demo.portfolio.repository.PortfolioRepository;
import com.example.demo.profiles.entity.Profile;
import com.example.demo.profiles.repository.ProfileRepository;
import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.roles.repository.RoleRepository;
import com.example.demo.shared.exception.BadRequestException;
import com.example.demo.shared.exception.ForbiddenException;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, 
                         PortfolioRepository portfolioRepository, ProfileRepository profileRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.portfolioRepository = portfolioRepository;
        this.profileRepository = profileRepository;
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
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public UserResponseDto create(CreateUserDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("El correo electrónico ya existe");
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
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new BadRequestException("Rol por defecto USER no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(defaultRole);
            return roles;
        }
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rol no encontrado: " + roleName));
            roles.add(role);
        }
        return roles;
    }

    @Override
    @Transactional
    public UserResponseDto assignRoles(Long id, Set<RoleName> roleNames) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Proteger contra la eliminación del rol ADMIN al usuario ADMIN principal (id=1)
        if (id.equals(1L) && !roleNames.contains(RoleName.ADMIN)) {
            throw new ForbiddenException("No se puede eliminar el rol ADMIN del usuario administrador principal");
        }
        
        user.setRoles(resolveRoles(roleNames));
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto makeProgrammer(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Asignar rol PROGRAMADOR (mantener otros roles)
        Set<Role> roles = new HashSet<>(user.getRoles());
        Role programmerRole = roleRepository.findByName(RoleName.PROGRAMADOR)
            .orElseThrow(() -> new BadRequestException("Rol PROGRAMADOR no encontrado"));
        roles.add(programmerRole);
        user.setRoles(roles);
        
        // Crear Portfolio si no existe
        if (user.getPortfolio() == null) {
            Portfolio portfolio = new Portfolio();
            portfolio.setOwner(user);
            portfolioRepository.save(portfolio);
            user.setPortfolio(portfolio);
        }
        
        // Crear Profile si no existe
        if (user.getProfile() == null) {
            Profile profile = new Profile();
            profile.setUser(user);
            profile.setDisplayName(user.getUsername());
            profileRepository.save(profile);
            user.setProfile(profile);
        }
        
        return UserMapper.toResponse(userRepository.save(user));
    }
}