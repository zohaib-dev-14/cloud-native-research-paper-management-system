package com.zabisoft.research_paper_system_project.service;
import com.zabisoft.research_paper_system_project.dto.PendingRegistration;
import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;
import static com.zabisoft.research_paper_system_project.helper.KeyHelper.*;
import static com.zabisoft.research_paper_system_project.util.OTPGeneration.generateOTP;
import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import com.zabisoft.research_paper_system_project.enums.OTPType;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OTPService {
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public ApiResponse sendRegistrationOTP(RegisterRequest registerRequest) {


        PendingRegistration pendingRegisteration = new PendingRegistration();
        pendingRegisteration.setName(registerRequest.getName());
        pendingRegisteration.setEmail(registerRequest.getEmail());
        pendingRegisteration.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );
        pendingRegisteration.setRole(registerRequest.getRole());

        String registrationKey = registrationKey(registerRequest.getEmail());

        if(Boolean.TRUE.equals(
                redisTemplate.hasKey(registrationKey)
        )) {
            throw new RuntimeException(
                    "Registration already pending..."
            );
        }

        //hasKey() + set() -> are different and create race conditions so use setIfAbsent

        redisTemplate.opsForValue().setIfAbsent(registrationKey, pendingRegisteration, Duration.ofMinutes(5));

        SendOTPRequest sendOTPRequest = new SendOTPRequest();
        sendOTPRequest.setEmail(
                registerRequest.getEmail()
        );
        sendOTPRequest.setOtpType(OTPType.REGISTER);

        sendOTP(sendOTPRequest);

        return new ApiResponse(
                true, "OTP sent successfully"
        );
    }
    public ApiResponse resendRegistrationOTP(SendOTPRequest request) {
        String registrationKey = registrationKey(request.getEmail());
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(registrationKey))) {
            throw new RuntimeException("Registration expired");
        }

        request.setOtpType(OTPType.REGISTER);
        resendOTP(request);
        return new ApiResponse(
                true,
                "OTP resent successfully"
        );
    }

    public ApiResponse forgotPasswordOTP(SendOTPRequest sendOTPRequest) {
        if (!userRepository.existsByEmail(sendOTPRequest.getEmail())) {
            throw new RuntimeException("User doesn't exist");
        }
        sendOTPRequest.setOtpType(OTPType.FORGOT_PASSWORD);
        sendOTP(sendOTPRequest);
        return new ApiResponse(
                true,
                "OTP sent successfully"
        );
    }

    public ApiResponse resendForgotPasswordOTP(SendOTPRequest sendOTPRequest) {
        if (!userRepository.existsByEmail(sendOTPRequest.getEmail())) {
            throw new RuntimeException("User doesn't exist");
        }
        sendOTPRequest.setOtpType(OTPType.FORGOT_PASSWORD);
         resendOTP(sendOTPRequest);
         return new ApiResponse(
                 true,
                 "OTP resent successfully"
         );
    }

    public ApiResponse verifyForgotPasswordOTP(VerifyOTPRequest verifyOTPRequest) {
        verifyOTPRequest.setOtpType(OTPType.FORGOT_PASSWORD);
        verifyOTP(verifyOTPRequest);
        String resetAllowedKey = resetAllowedKey(verifyOTPRequest.getEmail());
        stringRedisTemplate.opsForValue().set(resetAllowedKey, "true", Duration.ofMinutes(10));
        return new ApiResponse(
                true,
                "OTP verified successfully"
        );
    }
    public void sendOTP(SendOTPRequest sendOTPRequest) {
        String key = otpKey(sendOTPRequest.getEmail(), sendOTPRequest.getOtpType());

        // check existing otp
        String existingOTP = stringRedisTemplate.opsForValue().get(key);

        if (existingOTP != null) {
            emailService.sendOtp(sendOTPRequest.getEmail(), existingOTP);
            return;
        }

        String otp = generateOTP();
        stringRedisTemplate.opsForValue().setIfAbsent(key, otp, Duration.ofMinutes(5));
        emailService.sendOtp(sendOTPRequest.getEmail(), otp);


    }

    public void resendOTP(SendOTPRequest sendOTPRequest) {
        String otpKey = otpKey(sendOTPRequest.getEmail(), sendOTPRequest.getOtpType());
        String resendKey = resendKey(sendOTPRequest.getEmail(), sendOTPRequest.getOtpType());

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(resendKey))) {
           throw new RuntimeException("Wait 30 seconds before trying again");
        }
        String otp = stringRedisTemplate.opsForValue().get(otpKey);
        if (otp == null) {
            otp = generateOTP();
            stringRedisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(5));
        }
        stringRedisTemplate.opsForValue().set(resendKey, "1", Duration.ofSeconds(30));
        emailService.sendOtp(sendOTPRequest.getEmail(), otp);
    }

    public void verifyOTP(VerifyOTPRequest verifyOTPRequest) {
        String key = otpKey(verifyOTPRequest.getEmail(), verifyOTPRequest.getOtpType());
        String storedOTP = stringRedisTemplate.opsForValue().get(key);

        if(storedOTP == null) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        if(!storedOTP.equals(
                verifyOTPRequest.getOtp()
        )) {
            throw new RuntimeException(
                    "Invalid OTP"
            );
        }
        stringRedisTemplate.delete(key);
    }
}
