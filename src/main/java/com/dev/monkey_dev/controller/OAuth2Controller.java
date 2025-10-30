package com.dev.monkey_dev.controller;

import com.dev.monkey_dev.config.JwtUtil;
import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.service.impl.CustomOAuth2UserService;
import com.dev.monkey_dev.service.impl.OAuth2UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Map;

/**
 * Custom OAuth2 controller to handle OAuth2 authentication flow manually.
 * This provides a more reliable alternative to Spring Security's OAuth2 client
 * configuration.
 */
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${app.oauth2.authorized-redirect-url}")
    private String authorizedRedirectUrl;

    /**
     * Initiates OAuth2 authorization flow by redirecting to Google's authorization
     * server.
     */
    @GetMapping("/authorization/google")
    public void initiateGoogleOAuth(HttpServletResponse response) throws IOException {
        String authorizationUrl = UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();

        response.sendRedirect(authorizationUrl);
    }

    /**
     * Test endpoint to verify controller is working.
     */
    @GetMapping("/test")
    public String test() {
        return "OAuth2Controller is working!";
    }

    /**
     * Handles the OAuth2 callback from Google (custom callback URL).
     */
    @GetMapping("/oauth2/callback/google")
    public void handleGoogleOAuth2Callback(@RequestParam("code") String code,
            @RequestParam(value = "error", required = false) String error,
            HttpServletResponse response) throws IOException {
        processOAuth2Callback(code, error, response);
    }

    /**
     * Handles the OAuth2 callback from Google (custom callback URL).
     */
    @GetMapping("/callback/google")
    public void handleGoogleCallback(@RequestParam("code") String code,
            @RequestParam(value = "error", required = false) String error,
            HttpServletResponse response) throws IOException {
        processOAuth2Callback(code, error, response);
    }

    /**
     * Common OAuth2 callback processing logic.
     */
    private void processOAuth2Callback(String code, String error, HttpServletResponse response) throws IOException {
        if (error != null) {
            log.error("OAuth2 error: {}", error);
            String errorUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUrl)
                    .queryParam("error", error)
                    .build()
                    .toUriString();
            response.sendRedirect(errorUrl);
            return;
        }

        try {
            // Exchange authorization code for access token
            String accessToken = exchangeCodeForToken(code);

            // Get user info from Google
            Map<String, Object> userInfo = getUserInfoFromGoogle(accessToken);

            // Process the OAuth2 user
            OAuth2UserPrincipal oAuth2UserPrincipal = customOAuth2UserService.loadUser("google", userInfo);
            Users user = oAuth2UserPrincipal.getUser();

            // Create SecurityUser for token generation
            SecurityUser securityUser = new SecurityUser(user);

            // Generate JWT token
            String token = jwtUtil.doGenerateToken(securityUser);

            // Redirect to frontend with token
            String redirectUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUrl)
                    .queryParam("token", token)
                    .queryParam("type", "Bearer")
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);

        } catch (Exception ex) {
            log.error("Error processing OAuth2 callback", ex);
            String errorUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUrl)
                    .queryParam("error", ex.getMessage())
                    .build()
                    .toUriString();
            response.sendRedirect(errorUrl);
        }
    }

    /**
     * Exchanges authorization code for access token.
     */
    private String exchangeCodeForToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, Object> requestBody = Map.of(
                "client_id", googleClientId,
                "client_secret", googleClientSecret,
                "code", code,
                "grant_type", "authorization_code",
                "redirect_uri", googleRedirectUri);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(tokenUrl, requestBody, Map.class);
        if (response == null) {
            throw new RuntimeException("Failed to exchange code for token");
        }
        return (String) response.get("access_token");
    }

    /**
     * Gets user information from Google using the access token.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserInfoFromGoogle(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
        return restTemplate.getForObject(userInfoUrl, Map.class);
    }
}
