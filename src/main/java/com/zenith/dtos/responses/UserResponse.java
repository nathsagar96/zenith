package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "User response")
public record UserResponse(
        @Schema(description = "User ID", example = "1") Long id,
        @Schema(description = "Username", example = "john_doe") String username,
        @Schema(description = "User email", example = "john@example.com") String email,
        @Schema(description = "User first name", example = "John") String firstName,
        @Schema(description = "User last name", example = "Doe") String lastName,
        @Schema(description = "User bio", example = "Software developer") String bio,
        @Schema(description = "User role", example = "USER") String role,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of posts by this user", example = "5") int postCount,
        @Schema(description = "Number of comments by this user", example = "10") int commentCount) {}
