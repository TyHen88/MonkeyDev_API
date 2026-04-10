package com.ezcart.api.config;

import com.ezcart.api.common.api.StatusCode;
import com.ezcart.api.exception.BusinessException;
import com.ezcart.api.common.crypto.PasswordCipher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider {
    private final AuthenticationManager authenticationManager;

    public UserAuthenticationProvider(@Qualifier("userAuthProvider") AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // Not having access to the user's password
    public Authentication authenticate(String username, String password) throws Exception {
        try {
            var rawPwd = password;
            try {
                rawPwd = PasswordCipher.decrypt(password);
            } catch (Exception ignored) {
                // Accept plaintext passwords as well as encrypted payloads.
            }
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPwd));
        } catch (UsernameNotFoundException ex) {
            throw new BusinessException(StatusCode.INCORRECT_PASSWORD);
        } catch (BadCredentialsException e) {
            throw new BusinessException(StatusCode.INCORRECT_PASSWORD);
        } catch (DisabledException e) {
            throw new BusinessException(StatusCode.INACTIVE_USER);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.CREDENTIALS_REQUIRED);
        }
    }
}

