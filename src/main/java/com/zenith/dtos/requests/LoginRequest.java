package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for user login")
public record LoginRequest(
        @Schema(description = "Username of the user", example = "john_doe")
                @Size(max = 50, message = "Username must be less than {max} characters")
                String username,
        @Schema(description = "Email of the user", example = "john@example.com")
                @Size(max = 100, message = "Email must be less than {max} characters")
                String email,
        @Schema(
                        description = "Password of the user",
                        example = "password123",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String password) {}
