package com.dev.monkey_dev.config;

import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.service.impl.OAuth2UserPrincipal;
import com.dev.monkey_dev.domain.entity.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public OAuth2AuthenticationSuccessHandler(@Lazy JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Value("${app.oauth2.authorized-redirect-url:http://localhost:3333/oauth2/redirect}")
    private String authorizedRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {

        // Get the authenticated user
        OAuth2UserPrincipal oAuth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        Users user = oAuth2UserPrincipal.getUser();

        // Create SecurityUser for token generation
        SecurityUser securityUser = new SecurityUser(user);

        // Generate JWT token
        String token = jwtUtil.doGenerateToken(securityUser);

        // Redirect to frontend with token
        return UriComponentsBuilder.fromUriString(authorizedRedirectUrl)
                .queryParam("token", token)
                .queryParam("type", "Bearer")
                .build().toUriString();
    }
}