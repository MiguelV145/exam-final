package com.example.demo.security;

import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Intentar cargar por username primero
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> {
                    // Si no encuentra por username, intentar por email
                    return userRepository.findByEmail(identifier)
                            .orElseThrow(() -> new UsernameNotFoundException(
                                    "Usuario no encontrado con identifier: " + identifier));
                });
        
        return user;
    }
}
