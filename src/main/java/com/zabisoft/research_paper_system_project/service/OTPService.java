package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;

import static com.zabisoft.research_paper_system_project.helper.KeyHelper.*;
import static com.zabisoft.research_paper_system_project.util.OTPGeneration.generateOTP;

import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public OTPService(EmailService emailService, StringRedisTemplate redisTemplate) {
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }
    public void sendRegisterationOTP(RegisterRequest registerRequest) {

        String registerationOTP = registerationKey(registerRequest.getEmail());

    }

    public String sendOTP(SendOTPRequest sendOTPRequest) {
        String key = otpKey(sendOTPRequest.getEmail());

        // check existing otp
        String existingOTP = (String) redisTemplate.opsForValue().get(key);

        if (existingOTP != null) {
            emailService.sendOtp(sendOTPRequest.getEmail(), existingOTP);
            redisTemplate.opsForValue().set(key, existingOTP, 5, TimeUnit.MINUTES);
            return "OTP already sent, enter a valid OTP";
        }

        String otp = generateOTP();
        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);
        emailService.sendOtp(sendOTPRequest.getEmail(), otp);
        return "New OTP sent, please enter a valid otp";
    }

    public String resendOTP(SendOTPRequest sendOTPRequest) {
        String otpKey = otpKey(sendOTPRequest.getEmail());
        String resendKey = resendKey(sendOTPRequest.getEmail());

        if (Boolean.TRUE.equals(redisTemplate.hasKey(resendKey))) {
            return "Wait for 30 second before trying again!";
        }
        String otp = (String) redisTemplate.opsForValue().get(otpKey);
        if (otp == null) {
            otp = generateOTP();
            redisTemplate.opsForValue().set(otpKey, otp, 5, TimeUnit.MINUTES);
        }
        redisTemplate.opsForValue().set(resendKey, "1", 30, TimeUnit.SECONDS);

        emailService.sendOtp(sendOTPRequest.getEmail(), otp);
        return "OTP resent";
    }

    public String verifyOTP(VerifyOTPRequest verifyOTPRequest) {
        String key = otpKey(verifyOTPRequest.getEmail());
        String storedOTP = (String) redisTemplate.opsForValue().get(key);

        if (storedOTP == null) {
            return "OTP Expired";
        }

        if (storedOTP.equals(verifyOTPRequest.getOtp())) {

            //delete otp
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set(verifiedKey(verifyOTPRequest.getEmail()), "true", 1, TimeUnit.HOURS);
            return "OTP Verified";
        }

        return "Invalid OTP";
    }
}
