package com.zabisoft.research_paper_system_project.seeder;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


public class TemporaryPasswordGenerator implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    public TemporaryPasswordGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String @NonNull ... args) {
        System.out.println(
                UUID.randomUUID()
        );
        System.out.println(
                passwordEncoder.encode("AIReviewer2026")
        );
    }
}
