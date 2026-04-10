package com.ezcart.api.service.auth;

import com.ezcart.api.common.password.PasswordEncryption;
import com.ezcart.api.config.UserAuthenticationProvider;
import com.ezcart.api.config.JwtUtil;
import com.ezcart.api.domain.entity.RefreshToken;
import com.ezcart.api.domain.entity.Role;
import com.ezcart.api.domain.entity.SecurityUser;
import com.ezcart.api.domain.entity.Users;
import com.ezcart.api.domain.respository.RefreshTokenRepository;
import com.ezcart.api.domain.respository.RoleRepository;
import com.ezcart.api.domain.respository.UserRepository;
import com.ezcart.api.dto.request.RegisterRequestDto;
import com.ezcart.api.exception.BusinessException;
import com.ezcart.api.common.api.StatusCode;
import com.ezcart.api.helper.AuthHelper;
import com.ezcart.api.payload.auth.LoginRequest;
import com.ezcart.api.payload.auth.RefreshTokenRequest;
import com.ezcart.api.payload.auth.ResetPasswordRequest;
import com.ezcart.api.payload.auth.SetUpPasswordRequest;
import com.ezcart.api.payload.auth.UpdatePasswordRequest;
import com.ezcart.api.payload.auth.AuthResponse;
import com.ezcart.api.dto.request.UserAdminRequestDto;
import com.ezcart.api.enums.AuthProvider;
import com.ezcart.api.common.crypto.PasswordCipher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%&*?";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncryption passwordEncryption;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object login(LoginRequest request) throws Throwable {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException(StatusCode.CREDENTIALS_REQUIRED);
        }

        Authentication authentication = userAuthenticationProvider.authenticate(
                request.getUsername(),
                request.getPassword());

        // Be defensive about the principal type to avoid ClassCastException leading to 500s
        Object principal = authentication.getPrincipal();
        SecurityUser securityUser;
        if (principal instanceof SecurityUser su) {
            securityUser = su;
        } else if (principal instanceof UserDetails uds) {
            // Build our SecurityUser from the Users entity so downstream code that
            // expects SecurityUser works as intended
            String username = uds.getUsername();
            List<Users> usersByUsername = userRepository.findByUsername(username);
            if (usersByUsername.isEmpty()) {
                throw new BusinessException(StatusCode.USER_NOT_FOUND);
            }
            securityUser = new SecurityUser(usersByUsername.get(0));
        } else if (principal instanceof String usernameStr) {
            List<Users> usersByUsername = userRepository.findByUsername(usernameStr);
            if (usersByUsername.isEmpty()) {
                throw new BusinessException(StatusCode.USER_NOT_FOUND);
            }
            securityUser = new SecurityUser(usersByUsername.get(0));
        } else {
            throw new BusinessException(StatusCode.INCORRECT_PASSWORD);
        }

        if (securityUser == null) {
            throw new BusinessException(StatusCode.INCORRECT_PASSWORD);
        }

        if (!securityUser.isEnabled()) {
            throw new BusinessException(StatusCode.INACTIVE_USER);
        }

        Users user = userRepository.findWithRolesById(securityUser.getUserId())
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        SecurityUser tokenSecurityUser = new SecurityUser(user);

        // Revoke all existing refresh tokens for this user (optional: for security)
        refreshTokenRepository.revokeAllUserTokens(user);

        // Generate access token
        String accessToken;
        try {
            accessToken = jwtUtil.doGenerateToken(tokenSecurityUser);
        } catch (Exception e) {
            log.error("Failed to generate access token for user {}: {}", securityUser.getUsername(), e.getMessage(), e);
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }

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
    @Transactional(rollbackFor = Exception.class)
    public Object refreshToken(RefreshTokenRequest request) throws Throwable {
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            throw new BusinessException(StatusCode.REFRESH_TOKEN_REQUIRED);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException(StatusCode.INVALID_REFRESH_TOKEN));

        if (!refreshToken.isValid()) {
            throw new BusinessException(StatusCode.INVALID_REFRESH_TOKEN);
        }

        Users user = refreshToken.getUser();
        if (!user.isActive()) {
            throw new BusinessException(StatusCode.INACTIVE_USER);
        }

        Users tokenUser = userRepository.findWithRolesById(user.getId()).orElse(user);
        // Create SecurityUser for token generation
        SecurityUser securityUser = new SecurityUser(tokenUser);

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
                .user(tokenUser)
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
    @Transactional(rollbackFor = Exception.class)
    public void registerUser(UserAdminRequestDto requestDto) throws Throwable {
        if (requestDto.getEmail() != null && userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new BusinessException(StatusCode.EMAIL_ALREADY_EXISTS);
        }
        if (!userRepository.findByUsername(requestDto.getUsername()).isEmpty()) {
            throw new BusinessException(StatusCode.USERNAME_ALREADY_EXISTS);
        }
        String rawPassword;
        try {
            rawPassword = passwordEncryption.getPassword(requestDto.getPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED, e);
        }
        try {
            Users user = Users.builder()
                    .fullName(requestDto.getFullName())
                    .username(requestDto.getUsername())
                    .email(requestDto.getEmail())
                    .password(rawPassword)
                    .active(requestDto.getActive() != null ? requestDto.getActive() : true)
                    .authProvider(AuthProvider.LOCAL)
                    .build();
            user.setRoles(resolveRoles(requestDto));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequestDto requestDto) throws Throwable {
        if (requestDto == null) {
            throw new BusinessException(StatusCode.REGISTRATION_REQUEST_REQUIRED);
        }
        if (requestDto.username() == null || requestDto.username().isBlank()) {
            throw new BusinessException(StatusCode.USERNAME_REQUIRED);
        }
        if (requestDto.email() != null && userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new BusinessException(StatusCode.EMAIL_ALREADY_EXISTS);
        }
        if (!userRepository.findByUsername(requestDto.username()).isEmpty()) {
            throw new BusinessException(StatusCode.USERNAME_ALREADY_EXISTS);
        }

        String rawPassword;
        try {
            rawPassword = passwordEncryption.getPassword(requestDto.password());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED, e);
        }
        try {
            Users user = Users.builder()
                    .fullName(requestDto.fullName())
                    .username(requestDto.username())
                    .email(requestDto.email())
                    .password(rawPassword)
                    .active(true)
                    .authProvider(AuthProvider.LOCAL)
                    .build();
            user.setRoles(resolveRolesRegister(requestDto));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUpPassword(SetUpPasswordRequest request) throws Throwable {
        Long userId = AuthHelper.getUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Validate that new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_MISMATCH);
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
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordRequest request) throws Throwable {
        Long userId = AuthHelper.getUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Decrypt and verify old password
        String decryptedOldPassword;
        try {
            decryptedOldPassword = PasswordCipher.decrypt(request.getOldPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED);
        }

        if (!passwordEncryption.verifyPassword(decryptedOldPassword, user.getPassword())) {
            throw new BusinessException(StatusCode.INCORRECT_CURRENT_PASSWORD);
        }

        // Validate that new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_MISMATCH);
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

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public String forgotPassword(String email) throws Throwable {
        if (email == null || email.isBlank()) {
            throw new BusinessException(StatusCode.EMAIL_REQUIRED);
        }
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new BusinessException(StatusCode.INACTIVE_USER);
        }
        return jwtUtil.generateResetToken(user.getEmail());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) throws Throwable {
        if (request == null || request.token() == null || request.token().isBlank()) {
            throw new BusinessException(StatusCode.RESET_TOKEN_REQUIRED);
        }
        if (!jwtUtil.isResetTokenValid(request.token())) {
            throw new BusinessException(StatusCode.INVALID_TOKEN);
        }
        if (request.newPassword() == null || request.confirmPassword() == null ||
                !request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException(StatusCode.PASSWORD_MISMATCH);
        }
        String email = jwtUtil.extractEmail(request.token());
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new BusinessException(StatusCode.INACTIVE_USER);
        }
        String encryptedPassword;
        try {
            encryptedPassword = passwordEncryption.getPassword(request.newPassword());
        } catch (Exception e) {
            throw new BusinessException(StatusCode.PASSWORD_ENCRYPTION_REQUIRED, e);
        }
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public String encryptPassword(String payload) throws Throwable {
        if (payload == null) {
            throw new BusinessException(StatusCode.PASSWORD_REQUIRED);
        }
        return PasswordCipher.encrypt(payload);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public String generatePassword(int length) {
        int finalLength = length <= 0 ? 12 : length;
        if (finalLength < 8 || finalLength > 64) {
            throw new BusinessException(StatusCode.PASSWORD_LENGTH_INVALID);
        }
        StringBuilder builder = new StringBuilder(finalLength);
        for (int i = 0; i < finalLength; i++) {
            int idx = SECURE_RANDOM.nextInt(PASSWORD_CHARS.length());
            builder.append(PASSWORD_CHARS.charAt(idx));
        }
        return builder.toString();
    }

    private Set<Role> resolveRolesRegister(RegisterRequestDto requestDto) {
        Set<String> roleNames = new HashSet<>();
        addRoleName(roleNames, requestDto.role());
        if (roleNames.isEmpty()) {
            roleNames.add("USER");
        }
        List<Role> roles = roleRepository.findAllByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new BusinessException(StatusCode.ROLE_NOT_FOUND);
        }
        return new HashSet<>(roles);
    }
    private Set<Role> resolveRoles(UserAdminRequestDto requestDto) {
        Set<String> roleNames = new HashSet<>();
        if (requestDto.getRoles() != null && !requestDto.getRoles().isEmpty()) {
            requestDto.getRoles().forEach(name -> addRoleName(roleNames, name));
        }
        addRoleName(roleNames, requestDto.getRole());
        if (roleNames.isEmpty()) {
            roleNames.add("USER");
        }
        List<Role> roles = roleRepository.findAllByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new BusinessException(StatusCode.ROLE_NOT_FOUND);
        }
        return new HashSet<>(roles);
    }

    private void addRoleName(Set<String> roleNames, String roleName) {
        if (roleName == null) {
            return;
        }
        String normalized = roleName.trim().toUpperCase();
        if (normalized.isBlank()) {
            return;
        }
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }
        if (!normalized.isBlank()) {
            roleNames.add(normalized);
        }
    }
}

