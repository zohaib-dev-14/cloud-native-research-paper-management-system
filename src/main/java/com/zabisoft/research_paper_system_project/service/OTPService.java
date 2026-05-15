package com.zabisoft.research_paper_system_project.service;
import com.zabisoft.research_paper_system_project.dto.PendingRegisteration;
import com.zabisoft.research_paper_system_project.dto.RegisterRequest;
import com.zabisoft.research_paper_system_project.dto.SendOTPRequest;
import static com.zabisoft.research_paper_system_project.helper.KeyHelper.*;
import static com.zabisoft.research_paper_system_project.util.OTPGeneration.generateOTP;
import com.zabisoft.research_paper_system_project.dto.VerifyOTPRequest;
import com.zabisoft.research_paper_system_project.enums.OTPType;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;

    public OTPService(EmailService emailService, RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, PasswordEncoder passwordEncoder)
    {
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.passwordEncoder = passwordEncoder;
    }
    public ApiResponse sendRegistrationOTP(RegisterRequest registerRequest) {

        String registrationKey = registrationKey(registerRequest.getEmail());
        PendingRegisteration pendingRegisteration = new PendingRegisteration();
        pendingRegisteration.setName(registerRequest.getName());
        pendingRegisteration.setEmail(registerRequest.getEmail());
        pendingRegisteration.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );
        pendingRegisteration.setRole(registerRequest.getRole());

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
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(registrationKey))) {
            throw new RuntimeException("Registration expired");
        }

        request.setOtpType(OTPType.REGISTER);
        resendOTP(request);
        return new ApiResponse(
                true,
                "OTP resent"
        );
    }


    public void sendOTP(SendOTPRequest sendOTPRequest) {
        String key = otpKey(sendOTPRequest.getEmail(), sendOTPRequest.getOtpType());

        // check existing otp
        String existingOTP = stringRedisTemplate.opsForValue().get(key);

        if (existingOTP != null) {
            emailService.sendOtp(sendOTPRequest.getEmail(), existingOTP);
            stringRedisTemplate.opsForValue().set(key, existingOTP, Duration.ofMinutes(5));
            throw new RuntimeException("OTP already sent");
        }

        String otp = generateOTP();
        stringRedisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(5));
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
