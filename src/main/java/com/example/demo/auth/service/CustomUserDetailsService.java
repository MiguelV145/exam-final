package com.example.demo.auth.service;

import com.example.demo.roles.entity.Role;
import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(Role::getName)
            .map(Enum::name)
            .map(name -> new SimpleGrantedAuthority("ROLE_" + name))
            .collect(Collectors.toList());
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .disabled(!user.isEnabled())
            .authorities(authorities)
            .build();
    }
}
