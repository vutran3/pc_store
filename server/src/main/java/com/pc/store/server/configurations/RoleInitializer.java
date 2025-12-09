package com.pc.store.server.configurations;

import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pc.store.server.dao.CustomerRepository;
import com.pc.store.server.dao.RoleRepository;
import com.pc.store.server.entities.Customer;
import com.pc.store.server.entities.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer {

    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initializeRoles() {
        return args -> {
            Role userRole;
            Role adminRole;

            if (roleRepository.count() == 0) {
                log.info("Initializing default roles...");

                userRole = Role.builder()
                        .name("USER")
                        .description("Default user role with basic permissions")
                        .build();

                adminRole = Role.builder()
                        .name("ADMIN")
                        .description("Administrator role with full permissions")
                        .build();

                roleRepository.save(userRole);
                roleRepository.save(adminRole);

                log.info("Default roles initialized successfully: USER and ADMIN");
            } else {
                log.info("Roles already exist, skipping role initialization");
                adminRole = roleRepository
                        .findById("ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            }

            if (!customerRepository.existsByUserName("admin")) {
                log.info("Creating default admin user...");

                Customer adminUser = Customer.builder()
                        .userName("admin")
                        .password(passwordEncoder.encode("123456"))
                        .firstName("System")
                        .lastName("Administrator")
                        .email("admin@system.com")
                        .roles(Set.of(adminRole))
                        .build();

                customerRepository.save(adminUser);

                log.info("Default admin user created successfully with username: admin");
                log.warn("SECURITY WARNING: Please change the default admin password immediately!");
            } else {
                log.info("Admin user already exists, skipping admin creation");
            }
        };
    }
}
