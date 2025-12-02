package com.dev.monkey_dev.service.impl;

import com.dev.monkey_dev.common.password.PasswordEncryption;
import com.dev.monkey_dev.service.auth.AuthService;

import com.dev.monkey_dev.config.UserAuthenticationProvider;
import com.dev.monkey_dev.config.JwtUtil;
import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.payload.auth.SetUpPasswordRequest;
import com.dev.monkey_dev.payload.auth.UpdatePasswordRequest;
import com.dev.monkey_dev.payload.auth.AuthResponse;
import com.dev.monkey_dev.dto.mapper.UserMapper;
import com.dev.monkey_dev.dto.request.UserAdminRequestDto;
import com.dev.monkey_dev.enums.AuthProvider;
import com.dev.monkey_dev.enums.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
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

        String token = jwtUtil.doGenerateToken(securityUser);
        return new AuthResponse(
                token,
                "Bearer",
                jwtUtil.getExpireIn());
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setUpPassword'");
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequest request) throws Throwable {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePassword'");
    }
}
