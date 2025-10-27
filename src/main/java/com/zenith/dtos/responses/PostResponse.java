package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Post response")
public record PostResponse(
        @Schema(description = "Post ID", example = "1") Long id,
        @Schema(description = "Post title", example = "My First Post") String title,
        @Schema(description = "Post slug", example = "my-first-post") String slug,
        @Schema(description = "Post content", example = "This is the content of my post") String content,
        @Schema(description = "Post status", example = "PUBLISHED") String status,
        @Schema(description = "ID of the author of this post", example = "1") Long authorId,
        @Schema(description = "ID of the category of this post", example = "1") Long categoryId,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of tags associated with this post", example = "3") int tagCount,
        @Schema(description = "Number of comments on this post", example = "5") int commentCount) {}
