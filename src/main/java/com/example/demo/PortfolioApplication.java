package com.example.demo;

import com.example.demo.roles.entity.Role;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.roles.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setName(RoleName.ADMIN);
                roleRepository.save(adminRole);

                Role programadorRole = new Role();
                programadorRole.setName(RoleName.PROGRAMADOR);
                roleRepository.save(programadorRole);

                Role userRole = new Role();
                userRole.setName(RoleName.USER);
                roleRepository.save(userRole);

                System.out.println("Roles initialized successfully");
            }
        };
    }
}
