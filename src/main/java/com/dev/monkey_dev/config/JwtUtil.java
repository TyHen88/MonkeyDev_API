package com.dev.monkey_dev.config;

import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.enums.Roles;
import com.dev.monkey_dev.properties.JwtProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtConfig;
    private final JwtDecoder jwtDecoder;
    private final long resetTokenExpiration = 15 * 60 * 1000; // 15 minutes
    private final String secret = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForSecurity123456789";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    public long getExpireIn() {
        return jwtConfig.expiration().getSeconds();
    }

    public long getResetTokenExpiration() {
        return resetTokenExpiration;
    }

    public String getSecret() {
        return secret;
    }

    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    private Instant extractExpiration(String token) {
        return jwtDecoder.decode(token).getExpiresAt();
    }

    public String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).isBefore(Instant.now());
        } catch (ExpiredJwtException e) {
            LoggerFactory.getLogger(JwtUtil.class).warn("Expired JWT token: {}", e.getMessage());
            return true; // Token is expired
        } catch (JwtException e) {
            LoggerFactory.getLogger(JwtUtil.class).error("Invalid JWT token: {}", e.getMessage());
            return true; // Token is invalid
        }
    }

    public String doGenerateToken(SecurityUser securityUser) {
        Instant instant = Instant.now();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", Objects.requireNonNull(securityUser.getUserId(), "User ID cannot be null"));
        claims.put("username", Objects.requireNonNull(securityUser.getUsername(), "Username cannot be null"));
        // claims.put("role", Objects.requireNonNull(securityUser.getAuthorities(),
        // "Authorities cannot be null"));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(securityUser.getUsername())
                .issuer(Roles.USER.name()) // Assuming issuer is the role of the user
                .claims(c -> c.putAll(claims))
                .issuedAt(instant)
                .expiresAt(instant.plus(jwtConfig.expiration().getSeconds(), ChronoUnit.SECONDS))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();

    }

    public String generateResetToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("type", "reset_password")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + resetTokenExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean isResetTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object type = claims.get("type");
            Date expiration = claims.getExpiration();
            LoggerFactory.getLogger(JwtUtil.class).info("Reset JWT token: {}", claims);

            return "reset_password".equals(type) && expiration != null && expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            LoggerFactory.getLogger(JwtUtil.class).warn("Expired reset JWT token: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            LoggerFactory.getLogger(JwtUtil.class).error("Invalid reset JWT token: {}", e.getMessage());
            return false;
        }
    }

}