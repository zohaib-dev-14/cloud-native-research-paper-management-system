package com.zabisoft.research_paper_system_project.service;
import com.zabisoft.research_paper_system_project.dto.*;
import com.zabisoft.research_paper_system_project.entities.RefreshToken;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.Role;
import com.zabisoft.research_paper_system_project.interfaces.EmailService;
import com.zabisoft.research_paper_system_project.repositories.RefreshTokenRepository;
import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import com.zabisoft.research_paper_system_project.response.GenericApiResponse;
import com.zabisoft.research_paper_system_project.response.AuthResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.zabisoft.research_paper_system_project.helper.KeyHelper.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OTPService otpService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;

    public GenericApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User Already Exists");
        }
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.REVIEWER) {
            throw new RuntimeException("Invalid Role Selection");
        }
        if (!request.getPassword().equals(request.getConfirmPassword()))
        {
            throw new RuntimeException("Passwords do not match");
        }
        return otpService.sendRegistrationOTP(request);
    }

    // use for DB transactions
    @Transactional
    public AuthResponse verifyRegistrationOTP(VerifyOTPRequest request) {
        // generic OTP verification
        otpService.verifyOTP(request);
        String registrationKey = registrationKey(request.getEmail());
        PendingRegistration pending = (PendingRegistration) redisTemplate.opsForValue().get(registrationKey);

        if(pending == null) {
            throw new RuntimeException(
                    "Registration expired"
            );
        }
        if(userRepository.existsByEmail(
                pending.getEmail()
        )) {
            throw new RuntimeException(
                    "User already verified"
            );
        }
        User user = new User();
        user.setName(pending.getName());
        user.setEmail(pending.getEmail());
        user.setPassword(
                pending.getPassword()
        );
        user.setActive(true);
        user.setVerified(true);
        user.setRole(pending.getRole());
        userRepository.save(user);
        String accessToken = jwtService.generateToken(
                        user.getEmail(),
                        user.getRole().name()
        );
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        // cleanup temporary Redis state
        redisTemplate.delete(registrationKey);
        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    @Transactional
    public GenericApiResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String resetKey = resetAllowedKey(resetPasswordRequest.getEmail());
        String allowed = stringRedisTemplate.opsForValue().get(resetKey);
        if (allowed == null) {
            throw new RuntimeException("Reset permission expired");
        }

        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }


        User user = userRepository.findByEmail(
                resetPasswordRequest.getEmail()
                )
                .orElseThrow(
                () -> new RuntimeException(
                      "User not found"
                )
        );

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);
        stringRedisTemplate.delete(resetKey);
        refreshTokenRepository.deleteByEmail(user.getEmail());

        emailService.sendResetConfirmation(user.getEmail(), user.getName());

        return new GenericApiResponse(
                true,
                "Password reset successfully. Please login again."
        );

    }


    // 🔥 LOGIN
    public AuthResponse login(
            LoginRequest request
    ) throws AuthenticationException{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

           User user =  userRepository.findByEmail(request.getEmail()).orElseThrow(
                   () -> new RuntimeException("User Not Exists")
           );

           String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
           RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
   //* Refresh Token
    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        User user = userRepository.findByEmail(refreshToken.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found with this email")
        );

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
                newAccessToken,
                refreshToken.getToken()
        );
    }

    @Transactional
    public GenericApiResponse logout(LogoutRequest logoutRequest) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(logoutRequest.getRefreshToken());
        refreshTokenRepository.delete(refreshToken);
        return new GenericApiResponse(
                true,
                "Logout successful"
        );
    }
}