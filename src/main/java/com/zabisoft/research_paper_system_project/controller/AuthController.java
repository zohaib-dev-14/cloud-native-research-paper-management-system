package com.zabisoft.research_paper_system_project.controller;

import com.zabisoft.research_paper_system_project.dto.LoginRequest;
import com.zabisoft.research_paper_system_project.dto.RefreshTokenRequest;
import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.entities.RefreshToken;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.repositories.RefreshTokenRepository;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import com.zabisoft.research_paper_system_project.response.AuthResponse;
import com.zabisoft.research_paper_system_project.service.AuthService;
import com.zabisoft.research_paper_system_project.service.JWTService;
import com.zabisoft.research_paper_system_project.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JWTService jwtService;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(JWTService jwtService, AuthService authService, RefreshTokenService refreshTokenService, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(201).body(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(Response.SC_OK).body(authService.login(loginRequest));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        // verify token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        User user = userRepository.findByEmail(refreshToken.getEmail()).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.status(200).body(
                new AuthResponse(
                        newAccessToken,
                        refreshToken.getToken()
                )
        );

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestParam String email) {

        refreshTokenRepository.deleteByEmail(email);
        return ResponseEntity.status(200).body("Logout Successfully");
    }

}
