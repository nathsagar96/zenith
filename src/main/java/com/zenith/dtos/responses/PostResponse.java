package com.zenith.dtos.responses;

import com.zenith.enums.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object for a post")
public record PostResponse(
        @Schema(description = "ID of the post", example = "123e4567-e89b-12d3-a456-426614174000") UUID postId,
        @Schema(description = "Title of the post", example = "Getting Started with Spring Boot") String title,
        @Schema(description = "Content of the post", example = "This is the content of the post") String content,
        @Schema(description = "Status of the post", example = "PUBLISHED") PostStatus status,
        @Schema(description = "ID of the author", example = "123e4567-e89b-12d3-a456-426614174000") UUID authorId,
        @Schema(description = "ID of the category", example = "123e4567-e89b-12d3-a456-426614174000") UUID categoryId,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of tags", example = "5") Integer tagCount,
        @Schema(description = "Number of comments", example = "10") Integer commentCount) {}
