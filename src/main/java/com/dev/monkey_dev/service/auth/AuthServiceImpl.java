package com.dev.monkey_dev.service.auth;

import com.dev.monkey_dev.common.password.PasswordEncryption;
import com.dev.monkey_dev.config.UserAuthenticationProvider;
import com.dev.monkey_dev.config.JwtUtil;
import com.dev.monkey_dev.domain.entity.RefreshToken;
import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.RefreshTokenRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.helper.AuthHelper;
import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.payload.auth.RefreshTokenRequest;
import com.dev.monkey_dev.payload.auth.SetUpPasswordRequest;
import com.dev.monkey_dev.payload.auth.UpdatePasswordRequest;
import com.dev.monkey_dev.payload.auth.AuthResponse;
import com.dev.monkey_dev.dto.mapper.UserMapper;
import com.dev.monkey_dev.dto.request.UserAdminRequestDto;
import com.dev.monkey_dev.enums.AuthProvider;
import com.dev.monkey_dev.enums.Roles;

import com.dev.monkey_dev.util.PasswordUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncryption passwordEncryption;

    @Override
    @Transactional
    public Object login(LoginRequest request) throws Throwable {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Username and password are required");
        }

        Authentication authentication = userAuthenticationProvider.authenticate(
                request.getUsername(),
                request.getPassword());

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        if (securityUser == null) {
            throw new BusinessException(StatusCode.AUTHENTICATION_FAILED, "Authentication failed");
        }

        if (!securityUser.isEnabled()) {
            throw new BusinessException(StatusCode.INACTIVE_USER, "User account is disabled");
        }

        Users user = userRepository.findById(securityUser.getUserId())
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND, "User not found"));

        // Revoke all existing refresh tokens for this user (optional: for security)
        refreshTokenRepository.revokeAllUserTokens(user);

        // Generate access token
        String accessToken = jwtUtil.doGenerateToken(securityUser);

        // Generate and save refresh token
        String refreshTokenString = jwtUtil.generateRefreshToken();
        Instant expiresAt = jwtUtil.getRefreshTokenExpiration();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiresAt(expiresAt)
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                "Bearer",
                jwtUtil.getExpireIn(),
                refreshTokenString);
    }

    @Override
    @Transactional
    public Object refreshToken(RefreshTokenRequest request) throws Throwable {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Refresh token is required");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(StatusCode.INVALID_REFRESH_TOKEN, "Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new BusinessException(StatusCode.INVALID_REFRESH_TOKEN, "Refresh token is expired or revoked");
        }

        Users user = refreshToken.getUser();
        if (!user.isActive()) {
            throw new BusinessException(StatusCode.INACTIVE_USER, "User account is disabled");
        }

        // Create SecurityUser for token generation
        SecurityUser securityUser = new SecurityUser(user);

        // Generate new access token
        String newAccessToken = jwtUtil.doGenerateToken(securityUser);

        // Optionally rotate refresh token (for better security)
        // Revoke old refresh token
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Generate new refresh token
        String newRefreshTokenString = jwtUtil.generateRefreshToken();
        Instant expiresAt = jwtUtil.getRefreshTokenExpiration();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshTokenString)
                .expiresAt(expiresAt)
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return new AuthResponse(
                newAccessToken,
                "Bearer",
                jwtUtil.getExpireIn(),
                newRefreshTokenString);
    }

    @Override
    @Transactional
    public void registerUser(UserAdminRequestDto requestDto) throws Throwable {
        if (!userRepository.findByUsername(requestDto.getUsername()).isEmpty()) {
            throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
        }
        String rawPassword;
        try {
            rawPassword = passwordEncryption.getPassword(requestDto.getPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED, e);
        }
        Users user = Users.builder()
                .fullName(requestDto.getFullName())
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(rawPassword)
                .role(Roles
                        .valueOf(requestDto.getRole() != null ? requestDto.getRole().toUpperCase() : Roles.USER.name()))
                .active(requestDto.getActive() != null ? requestDto.getActive() : true)
                .authProvider(AuthProvider.LOCAL)
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void setUpPassword(SetUpPasswordRequest request) throws Throwable {
        Long userId = AuthHelper.getUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Validate that new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Password and confirm password do not match");
        }

        String rawPassword;
        try {
            rawPassword = passwordEncryption.getPassword(request.getNewPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED, e);
        }
        user.setPassword(rawPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request) throws Throwable {
        Long userId = AuthHelper.getUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND, "User not found"));

        // Decrypt and verify old password
        String decryptedOldPassword;
        try {
            decryptedOldPassword = PasswordUtils.decrypt(request.getOldPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED, "Failed to decrypt old password");
        }

        if (!passwordEncryption.verifyPassword(decryptedOldPassword, user.getPassword())) {
            throw new BusinessException(StatusCode.INCORRECT_CURRENT_PASSWORD, "Current password is incorrect");
        }

        // Validate that new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_MISMATCH, "New password and confirm password do not match");
        }

        String encryptedPassword;
        try {
            encryptedPassword = passwordEncryption.getPassword(request.getNewPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED);
        }
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }
}
