package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.dto.LoginRequest;
import com.zabisoft.research_paper_system_project.dto.RefreshTokenRequest;
import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.entities.RefreshToken;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Role;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import com.zabisoft.research_paper_system_project.response.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }
    // 🔥 REGISTER
//    public AuthResponse register(RegisterRequest request) {
//        if(userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException(
//                    "User already exists"
//            );
//        }
//        // ONLY public roles allowed
//        if(request.getRole() == Role.ADMIN || request.getRole() == Role.REVIEWER) {
//
//            throw new RuntimeException(
//                    "Invalid role selection"
//            );
//        }
//        User user = new User();
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setVerified(false);
//        user.setActive(true);
//        userRepository.save(user);
//        String accessToken =
//                jwtService.generateToken(
//                        user.getEmail(),
//                        user.getRole().name()
//                );
//        RefreshToken refreshToken =
//                refreshTokenService
//                        .createRefreshToken(
//                                user.getEmail()
//                        );
//        return new AuthResponse(
//                accessToken,
//                refreshToken.getToken()
//        );
//    }


    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User Already Exists");
        }

        if (request.getRole() == Role.ADMIN || request.getRole() == Role.REVIEWER) {
            throw new RuntimeException("Invalid Role Selection");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setVerified(false);

        userRepository.save(user);

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                user.getEmail()
        );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
    // 🔥 LOGIN
    public AuthResponse login(
            LoginRequest request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
           User user =  userRepository.findByEmail(request.getEmail()).orElseThrow(
                   () -> new RuntimeException("User Not Exists")
           );
           String accessToken = jwtService.generateToken(user.getEmail(), user.getPassword());
           RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
           return new AuthResponse(
                   accessToken,
                   refreshToken.getToken()
           );



        }
        throw new RuntimeException("Login Unsuccessful");
    }
    // 🔥 REFRESH TOKEN
    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {

//        RefreshToken refreshToken =
//                refreshTokenService
//                        .verifyRefreshToken(
//                                request.getRefreshToken()
//                        );
//
//
//
//        User user = userRepository
//                .findByEmail(
//                        refreshToken.getEmail()
//                )
//                .orElseThrow(
//                        () -> new RuntimeException(
//                                "User not found"
//                        )
//                );
//
//        String newAccessToken =
//                jwtService.generateToken(
//                        user.getEmail(),
//                        user.getRole().name()
//                );
//        return new AuthResponse(
//                newAccessToken,
//                refreshToken.getToken()
//        );
//    }


        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        User user = userRepository.findByEmail(refreshToken.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found with this email")
        );

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
                newAccessToken,
                refreshToken.getToken()
        );
    }
}