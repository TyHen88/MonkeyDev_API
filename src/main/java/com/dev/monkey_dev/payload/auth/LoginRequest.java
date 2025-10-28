package com.dev.monkey_dev.payload.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "User login request")
public class LoginRequest {
    @JsonProperty("user_name")
    @Schema(description = "Username for login", example = "john_doe", required = true)
    private String username;

    @JsonProperty("password")
    @Schema(description = "Password for login", example = "securePassword123", required = true)
    private String password;
}
