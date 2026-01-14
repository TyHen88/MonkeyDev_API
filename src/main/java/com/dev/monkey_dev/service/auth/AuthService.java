package com.dev.monkey_dev.service.auth;

import com.dev.monkey_dev.dto.request.UserAdminRequestDto;
import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.payload.auth.RefreshTokenRequest;
import com.dev.monkey_dev.payload.auth.SetUpPasswordRequest;
import com.dev.monkey_dev.payload.auth.UpdatePasswordRequest;

public interface AuthService {

    void registerUser(UserAdminRequestDto requestDto) throws Throwable;

    Object login(LoginRequest request) throws Throwable;

    Object refreshToken(RefreshTokenRequest request) throws Throwable;

    void setUpPassword(SetUpPasswordRequest request) throws Throwable;

    void updatePassword(UpdatePasswordRequest request) throws Throwable;
}
