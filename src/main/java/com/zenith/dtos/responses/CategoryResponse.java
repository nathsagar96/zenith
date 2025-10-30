package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object for a category")
public record CategoryResponse(
        @Schema(description = "ID of the category", example = "123e4567-e89b-12d3-a456-426614174000") UUID categoryId,
        @Schema(description = "Name of the category", example = "Technology") String name,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of posts in this category", example = "10") Integer postCount) {}
