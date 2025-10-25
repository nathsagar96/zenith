package com.zenith.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
                @Size(max = 50, message = "Username must be less than {max} characters")
                String username,
        @Email(message = "Email must be valid") @Size(max = 100, message = "Email must be less than {max} characters")
                String email,
        @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters") String password,
        @Size(max = 50, message = "First name must be less than {max} characters") String firstName,
        @Size(max = 50, message = "Last name must be less than {max} characters") String lastName,
        String bio,
        String role) {}
