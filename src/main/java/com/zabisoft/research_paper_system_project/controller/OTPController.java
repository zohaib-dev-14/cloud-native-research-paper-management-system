package com.zabisoft.research_paper_system_project.controller;


import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;
import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import com.zabisoft.research_paper_system_project.service.OTPService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
public class OTPController {
    private final OTPService otpService;



    public OTPController( OTPService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {

       return ResponseEntity.status(201).body(otpService.sendOTP(sendOTPRequest));


    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendOTP(@Valid @RequestBody SendOTPRequest sendOTPRequest) {
        return ResponseEntity.status(201).body(otpService.resendOTP(sendOTPRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody VerifyOTPRequest verifyOTPRequest)
    {
        return ResponseEntity.status(200).body(
                otpService.verifyOTP(verifyOTPRequest)
        );

    }
}
