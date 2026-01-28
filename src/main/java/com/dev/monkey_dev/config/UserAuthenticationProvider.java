package com.dev.monkey_dev.config;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.common.crypto.PasswordCipher;
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

    // Not having access to the user’s password
    public Authentication authenticate(String username, String password) throws Exception {
        try {
            var rawPwd = PasswordCipher.decrypt(password);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPwd));
        } catch (UsernameNotFoundException ex) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, ex.getMessage());
        } catch (BadCredentialsException e) {
            // throw new BadCredentialsException("Incorrect username or password", e);
            throw new BusinessException(StatusCode.INCORRECT_PASSWORD, e);
        } catch (DisabledException e) {
            throw new BusinessException(StatusCode.INACTIVE_USER, e);
        }
    }
}
