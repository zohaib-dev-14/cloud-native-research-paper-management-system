package com.zabisoft.research_paper_system_project.seeder;

import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Role;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


public class AdminSeeder
        implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String @NonNull ... args) {
        boolean adminExists = userRepository
                        .findByEmail("muhammadzohaibm271@gmail.com")
                        .isPresent();

        if (!adminExists) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("muhammadzohaibm271@gmail.com");
            admin.setPassword(passwordEncoder.encode("AdminZohaib2026"));
            admin.setRole(
                    Role.ADMIN
            );
            admin.setVerified(true);
            userRepository.save(admin);
        }
    }
}