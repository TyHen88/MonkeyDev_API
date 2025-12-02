package com.dev.monkey_dev.common.password;

import com.dev.monkey_dev.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordEncryptionImpl implements PasswordEncryption {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String getPassword(String password) throws Exception {
        var rawPassword = PasswordUtils.decrypt(password);
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public Boolean verifyPassword(String password, String hashedPassword) throws Exception {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
