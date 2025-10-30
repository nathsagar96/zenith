package com.zenith.dtos.responses;

import com.zenith.enums.CommentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object for a comment")
public record CommentResponse(
        @Schema(description = "ID of the comment", example = "123e4567-e89b-12d3-a456-426614174000") UUID commentId,
        @Schema(description = "Content of the comment", example = "This is a great post!") String content,
        @Schema(description = "Status of the comment", example = "APPROVED") CommentStatus status,
        @Schema(
                        description = "ID of the post this comment belongs to",
                        example = "123e4567-e89b-12d3-a456-426614174000")
                UUID postId,
        @Schema(description = "ID of the author of the comment", example = "123e4567-e89b-12d3-a456-426614174000")
                UUID authorId,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt) {}
