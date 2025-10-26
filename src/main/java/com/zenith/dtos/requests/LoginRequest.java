package com.zenith.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Size(max = 50, message = "Username must be less than {max} characters") String username,
        @Size(max = 100, message = "Email must be less than {max} characters") String email,
        @NotBlank(message = "Password is required")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String password) {}
