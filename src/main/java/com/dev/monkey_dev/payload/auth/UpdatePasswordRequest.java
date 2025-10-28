package com.dev.monkey_dev.payload.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Update password request")
public class UpdatePasswordRequest {
    @JsonProperty("old_password")
    @NotBlank
    @Schema(description = "Old password", example = "oldPassword123")
    private String oldPassword;
    @JsonProperty("new_password")
    @NotBlank
    @Schema(description = "New password", example = "newPassword123")
    private String newPassword;
    @JsonProperty("confirm_password")
    @NotBlank
    @Schema(description = "Confirm password", example = "newPassword123")
    private String confirmPassword;
}
