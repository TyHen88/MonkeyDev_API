package com.dev.monkey_dev.common.password;

import com.dev.monkey_dev.common.crypto.PasswordCipher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordEncryptionImpl implements PasswordEncryption {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String getPassword(String password) throws Exception {
        // Accept both encrypted and plaintext passwords.
        // If the client sends an encrypted value (expected in some flows), try to decrypt it first.
        // If decryption fails, assume the provided value is plaintext and encode it directly.
        String rawPassword = password;
        try {
            // Try to decrypt; PasswordCipher.decrypt throws on invalid input
            rawPassword = PasswordCipher.decrypt(password);
        } catch (Exception ex) {
            // Log at debug in case we need to investigate (avoid required logger here)
            // fall back to treating provided password as raw plaintext
        }
        return passwordEncoder.encode(rawPassword);
    }
    @Override
    public Boolean verifyPassword(String password, String hashedPassword) throws Exception {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
