package com.dev.monkey_dev.payload.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ResetPasswordRequest(
                @JsonProperty("token") @JsonAlias("session_id") @NotBlank String token,

                @JsonProperty("new_password") @NotBlank @Length(max = 50) String newPassword,

                @JsonProperty("confirm_password") @NotBlank @Length(max = 50) String confirmPassword

// @JsonProperty("otp_code")
// @NotBlank
// String otpCode

) {

}
