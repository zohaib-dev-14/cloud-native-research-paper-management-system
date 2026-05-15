package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.dto.*;
import com.zabisoft.research_paper_system_project.entities.RefreshToken;
import com.zabisoft.research_paper_system_project.entities.User;
import com.zabisoft.research_paper_system_project.enums.OTPType;
import com.zabisoft.research_paper_system_project.enums.Role;

import static com.zabisoft.research_paper_system_project.helper.KeyHelper.otpKey;
import static com.zabisoft.research_paper_system_project.helper.KeyHelper.registrationKey;

import com.zabisoft.research_paper_system_project.repositories.UserRepository;
import com.zabisoft.research_paper_system_project.response.ApiResponse;
import com.zabisoft.research_paper_system_project.response.AuthResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final OTPService otpService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;


    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            RefreshTokenService refreshTokenService, OTPService otpService, RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }
    // 🔥 REGISTER
//    public AuthResponse register(RegisterRequest request) {
//        if(userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException(
//                    "User already exists"
//            );
//        }
//        // ONLY public roles allowed
//        if(request.getRole() == Role.ADMIN || request.getRole() == Role.REVIEWER) {
//
//            throw new RuntimeException(
//                    "Invalid role selection"
//            );
//        }
//        User user = new User();
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setVerified(false);
//        user.setActive(true);
//        userRepository.save(user);
//        String accessToken =
//                jwtService.generateToken(
//                        user.getEmail(),
//                        user.getRole().name()
//                );
//        RefreshToken refreshToken =
//                refreshTokenService
//                        .createRefreshToken(
//                                user.getEmail()
//                        );
//        return new AuthResponse(
//                accessToken,
//                refreshToken.getToken()
//        );
//    }


    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User Already Exists");
        }
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User doesn't exist");
        }
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.REVIEWER) {
            throw new RuntimeException("Invalid Role Selection");
        }
        if (!request.getPassword().equals(request.getConfirmPassword()))
        {
            throw new RuntimeException("Passwords don't match");
        }
        return otpService.sendRegistrationOTP(request);
    }

    // use for DB transactions
    @Transactional
    public AuthResponse verifyRegistrationOTP(VerifyOTPRequest request) {
        // generic OTP verification
        otpService.verifyOTP(request);
        String registrationKey = registrationKey(request.getEmail());
        PendingRegisteration pending = (PendingRegisteration) redisTemplate.opsForValue().get(registrationKey);

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
        stringRedisTemplate.delete(
                otpKey(
                        request.getEmail(),
                        OTPType.REGISTER
                )
        );
        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
    // 🔥 LOGIN
    public AuthResponse login(
            LoginRequest request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
           User user =  userRepository.findByEmail(request.getEmail()).orElseThrow(
                   () -> new RuntimeException("User Not Exists")
           );
           String accessToken = jwtService.generateToken(user.getEmail(), user.getPassword());
           RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
           return new AuthResponse(
                   accessToken,
                   refreshToken.getToken()
           );



        }
        throw new RuntimeException("Login Unsuccessful");
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
}