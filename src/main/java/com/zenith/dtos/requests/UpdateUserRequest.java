package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for updating a user")
public record UpdateUserRequest(
        @Schema(description = "Updated username", example = "john_doe")
                @Pattern(
                        regexp = "^[a-zA-Z0-9_]+$",
                        message = "Username can only contain letters, numbers, and underscores")
                @Size(max = 50, message = "Username must be less than {max} characters")
                String username,
        @Schema(description = "Updated email", example = "john@example.com")
                @Email(message = "Email must be valid")
                @Size(max = 100, message = "Email must be less than {max} characters")
                String email,
        @Schema(description = "Updated password", example = "newpassword123")
                @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                String password,
        @Schema(description = "Updated first name", example = "John")
                @Size(max = 50, message = "First name must be less than {max} characters")
                String firstName,
        @Schema(description = "Updated last name", example = "Doe")
                @Size(max = 50, message = "Last name must be less than {max} characters")
                String lastName,
        @Schema(description = "Updated bio", example = "Software Developer") String bio) {}
