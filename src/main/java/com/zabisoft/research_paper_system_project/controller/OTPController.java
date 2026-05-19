package com.zabisoft.research_paper_system_project.controller;


import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;
import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import com.zabisoft.research_paper_system_project.service.AuthService;
import com.zabisoft.research_paper_system_project.service.OTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService otpService;

    @PostMapping("/resend-registration-otp")
    public ResponseEntity<ApiResponse> resendOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.status(200).body(otpService.resendRegistrationOTP(sendOTPRequest));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPasswordOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.status(200).body(
                otpService.forgotPasswordOTP(sendOTPRequest)
        );
    }

    @PostMapping("/resend-forgot-password")
    public ResponseEntity<ApiResponse> resendForgotPasswordOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.ok().body(
                otpService.resendForgotPasswordOTP(sendOTPRequest)
        );
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<ApiResponse> verifyForgotPasswordOTP(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest) {
        return ResponseEntity.ok().body(
                otpService.verifyForgotPasswordOTP(verifyOTPRequest)
        );
    }
}
