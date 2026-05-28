package com.zabisoft.research_paper_system_project.controller;


import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;
import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import com.zabisoft.research_paper_system_project.response.GenericApiResponse;
import com.zabisoft.research_paper_system_project.service.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Tag(
        name = "2. OTP APIs",
        description = "APIs for OTP verification and password recovery."
)
public class OTPController {
    private final OTPService otpService;


    @PostMapping("/resend-registration-otp")
    @Operation(
            summary = "Resend Registration OTP",
            description = "Resends registration OTP."
    )
    public ResponseEntity<GenericApiResponse> resendOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.status(200).body(otpService.resendRegistrationOTP(sendOTPRequest));
    }



    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot Password",
            description = "Sends OTP for password recovery."
    )
    public ResponseEntity<GenericApiResponse> forgotPasswordOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.status(200).body(
                otpService.forgotPasswordOTP(sendOTPRequest)
        );
    }

    @PostMapping("/resend-forgot-password")
    @Operation(
            summary = "Resend Forgot Password OTP",
            description = "Resends forgot password OTP."
    )
    public ResponseEntity<GenericApiResponse> resendForgotPasswordOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.ok().body(
                otpService.resendForgotPasswordOTP(sendOTPRequest)
        );
    }

    @Operation(
            summary = "Verify Forgot Password OTP",
            description = "Verifies forgot password OTP."
    )
    @PostMapping("/verify-forgot-password")
    public ResponseEntity<GenericApiResponse> verifyForgotPasswordOTP(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest) {
        return ResponseEntity.ok().body(
                otpService.verifyForgotPasswordOTP(verifyOTPRequest)
        );
    }
}
