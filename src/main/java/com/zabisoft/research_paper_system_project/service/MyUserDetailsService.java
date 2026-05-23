package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.principal.UserPrincipal;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public @NullMarked UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found")
        );
        return new UserPrincipal(user);
    }
}
