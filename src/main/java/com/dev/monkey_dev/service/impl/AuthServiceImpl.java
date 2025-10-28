package com.dev.monkey_dev.service.impl;

import com.dev.monkey_dev.service.auth.AuthService;

import com.dev.monkey_dev.config.UserAuthenticationProvider;
import com.dev.monkey_dev.config.JwtUtil;
import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.payload.auth.SetUpPasswordRequest;
import com.dev.monkey_dev.payload.auth.UpdatePasswordRequest;
import com.dev.monkey_dev.payload.auth.AuthResponse;

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
