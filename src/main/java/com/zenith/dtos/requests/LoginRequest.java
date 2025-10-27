package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User login request")
public record LoginRequest(
        @Size(max = 50, message = "Username must be less than {max} characters")
                @Schema(description = "Username", example = "john_doe")
                String username,
        @Size(max = 100, message = "Email must be less than {max} characters")
                @Schema(description = "User email", example = "john@example.com")
                String email,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                @Schema(
                        description = "User password",
                        example = "password123",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String password) {}
