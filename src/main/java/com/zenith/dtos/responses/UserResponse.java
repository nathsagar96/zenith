package com.zenith.dtos.responses;

import com.zenith.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object for a user")
public record UserResponse(
        @Schema(description = "ID of the user", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
        @Schema(description = "Username of the user", example = "john_doe") String username,
        @Schema(description = "Email of the user", example = "john@example.com") String email,
        @Schema(description = "First name of the user", example = "John") String firstName,
        @Schema(description = "Last name of the user", example = "Doe") String lastName,
        @Schema(description = "Bio of the user", example = "Software Developer") String bio,
        @Schema(description = "Role of the user", example = "USER") RoleType role,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of posts by the user", example = "5") Integer postCount,
        @Schema(description = "Number of comments by the user", example = "10") Integer commentCount) {}
