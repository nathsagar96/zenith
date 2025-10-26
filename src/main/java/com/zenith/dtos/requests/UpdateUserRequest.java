package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "User update request")
public record UpdateUserRequest(
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
                @Size(max = 50, message = "Username must be less than {max} characters")
                @Schema(description = "Updated username", example = "john_doe")
                String username,
        @Email(message = "Email must be valid")
                @Size(max = 100, message = "Email must be less than {max} characters")
                @Schema(description = "Updated email", example = "john@example.com")
                String email,
        @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters")
                @Schema(description = "Updated password", example = "newpassword123")
                String password,
        @Size(max = 50, message = "First name must be less than {max} characters")
                @Schema(description = "Updated first name", example = "John")
                String firstName,
        @Size(max = 50, message = "Last name must be less than {max} characters")
                @Schema(description = "Updated last name", example = "Doe")
                String lastName,
        @Schema(description = "Updated bio", example = "Updated bio information") String bio) {}
