package com.zabisoft.research_paper_system_project.service.authenticatedUserImplementation;

import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.interfaces.AuthenticatedUserService;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationUserServiceImpl implements AuthenticatedUserService {
    private final UserRepository userRepository;
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = null;
        if (authentication != null) {
            email = authentication.getName();
        }
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Authenticated user not found")
        );
    }
}
