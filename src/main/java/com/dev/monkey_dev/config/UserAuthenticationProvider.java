package com.dev.monkey_dev.config;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.util.PasswordUtils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

    /**
     * Authenticate a user with a username and password.
     * 
     * @param username the username
     * @param password the password
     * @return the authentication
     * @throws Exception if the authentication fails
     */
    @Retryable(value = { UsernameNotFoundException.class, BadCredentialsException.class,
            DisabledException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Authentication authenticate(String username, String password) throws Exception {
        var rawPwd = PasswordUtils.decrypt(password);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPwd));
    }

    /**
     * Handle authentication exceptions after retry attempts are exhausted.
     * This method will be called by Spring Retry when all retry attempts fail.
     */
    public Authentication authenticateWithExceptionHandling(String username, String password) throws Exception {
        try {
            return authenticate(username, password);
        } catch (UsernameNotFoundException ex) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND, ex.getMessage());
        } catch (BadCredentialsException e) {
            throw new BusinessException(StatusCode.INCORRECT_PASSWORD, e);
        } catch (DisabledException e) {
            throw new BusinessException(StatusCode.INACTIVE_USER, e);
        }
    }
}
