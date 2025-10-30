package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for user registration")
public record RegisterRequest(
        @Schema(description = "Username of the user", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Username is required")
                @Pattern(
                        regexp = "^[a-zA-Z0-9_]+$",
                        message = "Username can only contain letters, numbers, and underscores")
                @Size(max = 50, message = "Username must be less than {max} characters")
                String username,
        @Schema(
                        description = "Email of the user",
                        example = "john@example.com",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Email is required")
                @Email(message = "Email must be valid")
                @Size(max = 100, message = "Email must be less than {max} characters")
                String email,
        @Schema(
                        description = "Password of the user",
                        example = "SecurePass123!",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String password) {}
