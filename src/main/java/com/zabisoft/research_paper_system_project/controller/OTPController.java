package com.zabisoft.research_paper_system_project.controller;


import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;
import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import com.zabisoft.research_paper_system_project.service.AuthService;
import com.zabisoft.research_paper_system_project.service.OTPService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
public class OTPController {
    private final OTPService otpService;
    private final AuthService authService;


    public OTPController(OTPService otpService, AuthService authService) {
        this.otpService = otpService;
        this.authService = authService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOTP(@Valid @RequestBody RegisterRequest registerRequest) {
       return ResponseEntity.status(201).body(authService.register(registerRequest));
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.status(201).body(otpService.resendRegistrationOTP(sendOTPRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest)
    {
        return ResponseEntity.status(200).body(
                authService.verifyRegistrationOTP(verifyOTPRequest)
        );

    }
}
