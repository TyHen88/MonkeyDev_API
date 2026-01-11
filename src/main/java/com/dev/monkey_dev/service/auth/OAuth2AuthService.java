package com.dev.monkey_dev.service.auth;

import com.dev.monkey_dev.config.JwtUtil;
import com.dev.monkey_dev.domain.entity.SecurityUser;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.properties.OAuth2Properties;
import com.dev.monkey_dev.support.GoogleOAuth2Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthService {

    private final OAuth2Properties props;
    private final GoogleOAuth2Client googleClient;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;

    public String buildGoogleAuthorizationUrl() {
        return UriComponentsBuilder
                .fromUriString(props.getGoogle().getAuthUrl())
                .queryParam("client_id", props.getGoogle().getClientId())
                .queryParam("redirect_uri", props.getGoogle().getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
    }

    public String handleGoogleCallback(String code, String error) {
        if (error != null && !error.isBlank()) {
            log.warn("OAuth2 Google error: {}", error);
            return buildErrorRedirect(error);
        }
        if (code == null || code.isBlank()) {
            return buildErrorRedirect("missing_code");
        }

        try {
            String accessToken = googleClient.exchangeCodeForAccessToken(code);
            Map<String, Object> userInfo = googleClient.getUserInfo(accessToken);

            OAuth2UserPrincipal principal = customOAuth2UserService.loadUser("google", userInfo);
            Users user = principal.getUser();

            String jwt = jwtUtil.doGenerateToken(new SecurityUser(user));

            return UriComponentsBuilder.fromUriString(props.getAuthorizedRedirectUrl())
                    .queryParam("token", jwt)
                    .queryParam("type", "Bearer")
                    .build()
                    .toUriString();

        } catch (Exception ex) {
            log.error("Error processing Google OAuth2 callback", ex);
            return buildErrorRedirect("oauth2_failed");
        }
    }

    private String buildErrorRedirect(String error) {
        return UriComponentsBuilder.fromUriString(props.getAuthorizedRedirectUrl())
                .queryParam("error", error)
                .build()
                .toUriString();
    }
}
