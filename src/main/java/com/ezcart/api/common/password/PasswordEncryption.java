package com.ezcart.api.common.password;

public interface PasswordEncryption {
    String getPassword(String password) throws Exception;

    Boolean verifyPassword(String password, String hashedPassword) throws Exception;
}

