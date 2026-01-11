package com.dev.monkey_dev.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.service.auth.AuthService;
import com.dev.monkey_dev.util.PasswordUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to authentication (login, token, etc.)")
public class AuthenticationController extends BaseApiRestController {

    private final AuthService authService;

    /**
     * Authenticates the user and returns a JWT token if credentials are valid.
     *
     * @param request the login request containing username/email and password
     * @return authentication response containing JWT and optionally a refresh token
     */
    @Operation(summary = "Login to receive JWT access token", description = "Authenticate user with credentials and retrieve JWT token for secured API access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public Object login(@RequestHeader Map<String, String> headers, @RequestBody @Valid LoginRequest payload)
            throws Throwable {
        return created(authService.login(payload));
    }

    @PostMapping("/encrypt")
    public Object encryptPassword(@RequestBody @Valid String payload) throws Throwable {
        var passwordValid = payload.replace("\"", "");
        var passwordEncrypted = PasswordUtils.encrypt(passwordValid);
        return successMessage(passwordEncrypted);
    }
}
