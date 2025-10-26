package com.zenith.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String oldPassword,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String newPassword,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String confirmPassword) {}
