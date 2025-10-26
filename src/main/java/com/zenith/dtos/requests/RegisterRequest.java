package com.zenith.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
                @Pattern(
                        regexp = "^[a-zA-Z0-9_]+$",
                        message = "Username can only contain letters, numbers, and underscores")
                @Size(max = 50, message = "Username must be less than {max} characters")
                String username,
        @NotBlank(message = "Email is required")
                @Email(message = "Email must be valid")
                @Size(max = 100, message = "Email must be less than {max} characters")
                String email,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String password) {}
