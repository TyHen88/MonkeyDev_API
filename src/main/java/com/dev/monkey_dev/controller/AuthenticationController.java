package com.dev.monkey_dev.controller;

import java.util.Map;

import com.dev.monkey_dev.payload.auth.SetUpPasswordRequest;
import com.dev.monkey_dev.payload.auth.UpdatePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.payload.auth.LoginRequest;
import com.dev.monkey_dev.payload.auth.RefreshTokenRequest;
import com.dev.monkey_dev.payload.auth.ForgotPasswordRequest;
import com.dev.monkey_dev.payload.auth.ResetPasswordRequest;
import com.dev.monkey_dev.service.auth.AuthService;

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
            @ApiResponse(responseCode = "201", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public Object login(@RequestHeader Map<String, String> headers, @RequestBody @Valid LoginRequest payload)
            throws Throwable {
        return created(authService.login(payload));
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
    public Object refreshToken(@RequestBody @Valid RefreshTokenRequest request) throws Throwable {
        return success(authService.refreshToken(request));
    }

    @PostMapping("/encrypt")
    public Object encryptPassword(@RequestBody @Valid String payload) throws Throwable {
        var passwordValid = payload.replace("\"", "");
        var passwordEncrypted = authService.encryptPassword(passwordValid);
        return successMessage(passwordEncrypted);
    }

    @GetMapping("/generate-password")
    public ResponseEntity<?> generatePassword(@RequestParam(value = "length", required = false) Integer length) {
        int size = length != null ? length : 12;
        String generated = authService.generatePassword(size);
        return success(generated);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) throws Throwable {
        String token = authService.forgotPassword(request.email());
        return success(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) throws Throwable {
        authService.resetPassword(request);
        return successMessage("Password reset successfully");
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) throws Throwable {
        authService.updatePassword(request);
        return successMessage("Password updated successfully");
    }

    @PostMapping("/setup-password")
    public ResponseEntity<?> setupPassword(@RequestBody @Valid SetUpPasswordRequest payload) throws Throwable {
        authService.setUpPassword(payload);
        return successMessage("Password setup successfully");
    }

}
