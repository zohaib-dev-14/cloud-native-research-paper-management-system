package com.zabisoft.research_paper_system_project.seeder;

import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Role;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewerSeeder
        implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${REVIEWER_NAME}")
    private String reviewerName;

    @Value("${REVIEWER_EMAIL}")
    private String reviewerEmail;

    @Value("${REVIEWER_PASSWORD}")
    private String reviewerPassword;
    @Override
    public void run(String @NonNull ... args) {
        boolean reviewerExists = userRepository
                .findByEmail(reviewerName)
                .isPresent();

        if (!reviewerExists) {
            User admin = new User();
            admin.setName(reviewerName);
            admin.setEmail(reviewerEmail);
            admin.setPassword(passwordEncoder.encode(reviewerPassword));
            admin.setRole(
                    Role.ADMIN
            );
            admin.setVerified(true);
            userRepository.save(admin);
        }
    }
}