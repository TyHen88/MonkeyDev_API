package com.ezcart.api.service.auth;

import com.ezcart.api.dto.request.RegisterRequestDto;
import com.ezcart.api.dto.request.UserAdminRequestDto;
import com.ezcart.api.payload.auth.LoginRequest;
import com.ezcart.api.payload.auth.RefreshTokenRequest;
import com.ezcart.api.payload.auth.ResetPasswordRequest;
import com.ezcart.api.payload.auth.SetUpPasswordRequest;
import com.ezcart.api.payload.auth.UpdatePasswordRequest;

public interface AuthService {

    void registerUser(UserAdminRequestDto requestDto) throws Throwable;

    void register(RegisterRequestDto requestDto) throws Throwable;

    Object login(LoginRequest request) throws Throwable;

    Object refreshToken(RefreshTokenRequest request) throws Throwable;

    void setUpPassword(SetUpPasswordRequest request) throws Throwable;

    void updatePassword(UpdatePasswordRequest request) throws Throwable;

    String forgotPassword(String email) throws Throwable;

    void resetPassword(ResetPasswordRequest request) throws Throwable;

    String encryptPassword(String payload) throws Throwable;

    String generatePassword(int length);
}

