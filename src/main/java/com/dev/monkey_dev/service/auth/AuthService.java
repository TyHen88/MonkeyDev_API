package com.dev.monkey_dev.service.auth;

import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.payload.auth.SetUpPasswordRequest;
import com.dev.monkey_dev.payload.auth.UpdatePasswordRequest;

public interface AuthService {

    Object login(LoginRequest request) throws Throwable;

    void setUpPassword(SetUpPasswordRequest request) throws Throwable;

    void updatePassword(UpdatePasswordRequest request) throws Throwable;
}
