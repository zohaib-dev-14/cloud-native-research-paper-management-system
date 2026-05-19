package com.zabisoft.research_paper_system_project.controller;
import com.zabisoft.research_paper_system_project.dto.*;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import com.zabisoft.research_paper_system_project.response.AuthResponse;
import com.zabisoft.research_paper_system_project.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(201).body(authService.register(request));
    }
    @PostMapping("/verify-registration")
    public ResponseEntity<AuthResponse> verifyRegistrationOTP(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest) {
        return ResponseEntity.status(200).body(authService.verifyRegistrationOTP(verifyOTPRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.status(200).body(
                authService.refreshToken(request)
        );
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(200).body(authService.login(loginRequest));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        return ResponseEntity.status(200).body(
                authService.logout(logoutRequest)
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.status(200).body(
                authService.resetPassword(resetPasswordRequest)
        );
    }
}