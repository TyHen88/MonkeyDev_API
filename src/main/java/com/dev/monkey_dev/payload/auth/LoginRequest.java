package com.dev.monkey_dev.payload.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "User login request")
public class LoginRequest {
    @JsonProperty("username")
    @JsonAlias("user_name")
    @NotBlank
    @Schema(description = "Username or email for login", example = "john_doe", required = true)
    private String username;

    @JsonProperty("password")
    @NotBlank
    @Schema(description = "Password for login", example = "securePassword123", required = true)
    private String password;
}
