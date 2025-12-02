package com.dev.monkey_dev.common.password;

public interface PasswordEncryption {
    String getPassword(String password) throws Exception;

    Boolean verifyPassword(String password, String hashedPassword) throws Exception;
}
