package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.util.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private final String mySecretKey = System.getenv("JWT_SECRET");

    public String generateToken(String email, String role) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", role);

        return Jwts.builder()
                .claims()
                .subject(email)
                .add(claims)
                .issuedAt(DateUtil.currentDate())
                .expiration(DateUtil.expirationDateTime())
                .and()
                .signWith(getKey())
                .compact();
    }

    public Key getKey() {

        byte[] bytes = Decoders.BASE64.decode(mySecretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    // extract role from jwt token
    public String extractRole(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // extract username from jwt token
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, String email) {
        final String username = extractUserName(token);
        return (username.equals(email) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


}
