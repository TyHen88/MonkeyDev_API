package com.dev.monkey_dev.payload.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "User registration request")
public class AuthRequest {

    @JsonProperty("first_name")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @NotNull
    @JsonProperty("user_name")
    @Schema(description = "Username for the account", example = "john_doe", required = true)
    private String username;

    @JsonProperty("email")
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @JsonProperty("phone_number")
    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @NotNull
    @JsonProperty("password")
    @Schema(description = "Password for the account", example = "securePassword123", required = true)
    private String password;

    @JsonProperty("auth_provider")
    @Schema(description = "Authentication provider", example = "LOCAL", required = true)
    private String authProvider;
}
