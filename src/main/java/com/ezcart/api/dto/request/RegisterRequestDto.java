package com.ezcart.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User registration request")
public record RegisterRequestDto(
        @NotBlank(message = "Full name is required")
        @Schema(description = "Full name", example = "John Doe", required = true)
        String fullName,

        @NotBlank(message = "Username is required")
        @Schema(description = "Username", example = "john_doe", required = true)
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        @Schema(description = "Email address", example = "john.doe@example.com", required = true)
        String email,

        @NotBlank(message = "Password is required")
        @Schema(description = "Password", example = "securePassword123", required = true)
        String password,

        @Schema(description = "Requested role name", example = "USER")
        String role) {
}
