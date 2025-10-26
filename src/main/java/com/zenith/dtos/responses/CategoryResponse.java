package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Category response")
public record CategoryResponse(
        @Schema(description = "Category ID", example = "1") Long id,
        @Schema(description = "Category name", example = "Technology") String name,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of posts in this category", example = "10") int postCount) {}
