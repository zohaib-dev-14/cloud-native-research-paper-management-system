package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.entities.RefreshToken;
import com.zabisoft.research_paper_system_project.repositories.RefreshTokenRepository;
import com.zabisoft.research_paper_system_project.util.DateUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
   public RefreshToken createRefreshToken(String email) {
        refreshTokenRepository.deleteByEmail(email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(
                DateUtil.expiryDateForSevenDays()
        );
        refreshToken.setEmail(email);
        return refreshTokenRepository.save(refreshToken);

   }

   public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken  = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new RuntimeException("Token Not Found")
        );
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Token Revoked");
        }

        if (refreshToken.getExpiresAt().before(new Date())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token Expired. Please Login again!");
        }
        return refreshToken;
    }
}
