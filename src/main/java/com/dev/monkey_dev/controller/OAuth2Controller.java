package com.dev.monkey_dev.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;

import com.dev.monkey_dev.service.auth.OAuth2AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// /**
// * Custom OAuth2 controller to handle OAuth2 authentication flow manually.
// * This provides a more reliable alternative to Spring Security's OAuth2
// client
// * configuration.
// */
@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final OAuth2AuthService oAuth2AuthService;

    @GetMapping("/authorization/google")
    public void authorizeGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect(oAuth2AuthService.buildGoogleAuthorizationUrl());
    }

    @GetMapping("/callback/google")
    public void callbackGoogle(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            HttpServletResponse response) throws IOException {
        response.sendRedirect(oAuth2AuthService.handleGoogleCallback(code, error));
    }

    // If you still want to keep old path for compatibility:
    @GetMapping("/oauth2/callback/google")
    public void callbackGoogleLegacy(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            HttpServletResponse response) throws IOException {
        response.sendRedirect(oAuth2AuthService.handleGoogleCallback(code, error));
    }
}
