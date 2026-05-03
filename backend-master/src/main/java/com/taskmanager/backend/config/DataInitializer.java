package com.taskmanager.backend.config;

import com.taskmanager.backend.entity.User;
import com.taskmanager.backend.enums.Role;
import com.taskmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Automatically generates the constructor for dependencies
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@task.com").isEmpty()) {
            // Using the Builder pattern to avoid constructor/setter "symbol" issues
            User admin = User.builder()
                    .email("admin@task.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            System.out.println("--- System: Default Admin Account Created (admin@task.com) ---");
        }
    }
}