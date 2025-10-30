package com.zenith.dtos.requests;

import com.zenith.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for creating a user")
public record CreateUserRequest(
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
                String password,
        @Schema(description = "First name of the user", example = "John")
                @Size(max = 50, message = "First name must be less than {max} characters")
                String firstName,
        @Schema(description = "Last name of the user", example = "Doe")
                @Size(max = 50, message = "Last name must be less than {max} characters")
                String lastName,
        @Schema(description = "Bio of the user", example = "Software Developer") String bio,
        @Schema(description = "Role of the user", example = "USER") RoleType role) {}
