package com.example.demo.config;

import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.roles.repository.RoleRepository;
import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, 
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles si no existen
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("Rol " + roleName + " creado exitosamente.");
            }
        }

        // Crear usuario ADMIN inicial si no existe
        String adminUsername = System.getenv("ADMIN_USERNAME");
        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        // Valores por defecto si no se especifican variables de entorno
        if (adminUsername == null || adminUsername.isEmpty()) {
            adminUsername = "admin";
        }
        if (adminEmail == null || adminEmail.isEmpty()) {
            adminEmail = "admin@example.com";
        }
        if (adminPassword == null || adminPassword.isEmpty()) {
            adminPassword = "admin123";
        }

        // Verificar si el usuario ADMIN ya existe
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setEnabled(true);

            // Asignar rol ADMIN
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            System.out.println("Usuario ADMIN inicial creado exitosamente.");
            System.out.println("  Username: " + adminUsername);
            System.out.println("  Email: " + adminEmail);
        } else {
            System.out.println("Usuario ADMIN ya existe: " + adminUsername);
        }
    }
}
