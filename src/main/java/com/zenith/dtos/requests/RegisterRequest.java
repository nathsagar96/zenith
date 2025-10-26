package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record RegisterRequest(
        @NotBlank(message = "Username is required")
                @Pattern(
                        regexp = "^[a-zA-Z0-9_]+$",
                        message = "Username can only contain letters, numbers, and underscores")
                @Size(max = 50, message = "Username must be less than {max} characters")
                @Schema(description = "Username", example = "john_doe", required = true)
                String username,
        @NotBlank(message = "Email is required")
                @Email(message = "Email must be valid")
                @Size(max = 100, message = "Email must be less than {max} characters")
                @Schema(description = "User email", example = "john@example.com", required = true)
                String email,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                @Schema(description = "User password", example = "password123", required = true)
                String password) {}
