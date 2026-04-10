package com.ezcart.api.controller;

import java.util.Map;

import com.ezcart.api.payload.auth.SetUpPasswordRequest;
import com.ezcart.api.payload.auth.UpdatePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ezcart.api.controller.base.BaseApiRestController;
import com.ezcart.api.dto.request.RegisterRequestDto;
import com.ezcart.api.payload.auth.LoginRequest;
import com.ezcart.api.payload.auth.RefreshTokenRequest;
import com.ezcart.api.payload.auth.ForgotPasswordRequest;
import com.ezcart.api.payload.auth.ResetPasswordRequest;
import com.ezcart.api.service.auth.AuthService;

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
     * @param payload the login request containing username/email and password
     * @return authentication response containing JWT and optionally a refresh token
     */
    @Operation(summary = "Login to receive JWT access token", description = "Authenticate user with credentials and retrieve JWT token for secured API access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> login(@RequestHeader Map<String, String> headers, @RequestBody @Valid LoginRequest payload)
            throws Throwable {
        return success(authService.login(payload));
    }

    @Operation(summary = "Register a new user", description = "Create a new local user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/register")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> register(@RequestBody @Valid RegisterRequestDto payload) throws Throwable {
        authService.register(payload);
        return createdMessage("User registered successfully");
    }

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param request the refresh token request containing the refresh token
     * @return authentication response containing new JWT access token and refresh
     *         token
     */
    @Operation(summary = "Refresh access token", description = "Exchange a valid refresh token for a new access token and refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) throws Throwable {
        return success(authService.refreshToken(request));
    }

    @PostMapping("/encrypt")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> encryptPassword(@RequestBody @Valid String payload) throws Throwable {
        var passwordValid = payload.replace("\"", "");
        var passwordEncrypted = authService.encryptPassword(passwordValid);
        return successMessage(passwordEncrypted);
    }

    @GetMapping("/generate-password")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> generatePassword(@RequestParam(value = "length", required = false) Integer length) {
        int size = length != null ? length : 12;
        String generated = authService.generatePassword(size);
        return success(generated);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) throws Throwable {
        String token = authService.forgotPassword(request.email());
        return success(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) throws Throwable {
        authService.resetPassword(request);
        return successMessage("Password reset successfully");
    }

    @PatchMapping("/update-password")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) throws Throwable {
        authService.updatePassword(request);
        return successMessage("Password updated successfully");
    }

    @PostMapping("/setup-password")
    public ResponseEntity<com.ezcart.api.common.api.ApiResponse<Object>> setupPassword(@RequestBody @Valid SetUpPasswordRequest payload) throws Throwable {
        authService.setUpPassword(payload);
        return successMessage("Password setup successfully");
    }

}

