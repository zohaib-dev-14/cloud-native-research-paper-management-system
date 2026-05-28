package com.zabisoft.research_paper_system_project.controller;
import com.zabisoft.research_paper_system_project.dto.*;
import com.zabisoft.research_paper_system_project.response.GenericApiResponse;
import com.zabisoft.research_paper_system_project.response.AuthResponse;
import com.zabisoft.research_paper_system_project.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(
        name = "1. Authentication APIs",
        description = "APIs for authentication and JWT management."
)
public class AuthController {
    private final AuthService authService;
    @Operation(
            summary = "Register New User",
            description = "Registers a new user account."
    )
    @PostMapping("/register")
    public ResponseEntity<GenericApiResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(201).body(authService.register(request));
    }
    @PostMapping("/verify-registration")
    @Operation(
            summary = "Verify Registration OTP",
            description = "Verifies OTP for user registration."
    )

    public ResponseEntity<AuthResponse> verifyRegistrationOTP(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest) {
        return ResponseEntity.status(200).body(authService.verifyRegistrationOTP(verifyOTPRequest));
    }

    @Operation(
            summary = "Login User",
            description = "Authenticates user and returns JWT tokens."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(200).body(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout User",
            description = "Logs out authenticated user."
    )
    public ResponseEntity<GenericApiResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        return ResponseEntity.status(200).body(
                authService.logout(logoutRequest)
        );
    }


    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset Password",
            description = "Resets user password."
    )
    public ResponseEntity<GenericApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.status(200).body(
                authService.resetPassword(resetPasswordRequest)
        );
    }

    @Operation(
            summary = "Refresh JWT Token",
            description = "Generates new access token using refresh token."
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.status(200).body(
                authService.refreshToken(request)
        );
    }
}