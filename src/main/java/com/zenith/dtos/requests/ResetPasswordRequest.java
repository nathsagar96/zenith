package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Password reset request")
public record ResetPasswordRequest(
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                @Schema(description = "Current password", example = "oldpassword123", required = true)
                String oldPassword,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                @Schema(description = "New password", example = "newpassword123", required = true)
                String newPassword,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                @Schema(description = "Confirm new password", example = "newpassword123", required = true)
                String confirmPassword) {}
