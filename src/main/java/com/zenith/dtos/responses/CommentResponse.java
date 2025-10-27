package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Comment response")
public record CommentResponse(
        @Schema(description = "Comment ID", example = "1") Long id,
        @Schema(description = "Comment content", example = "This is a great post!") String content,
        @Schema(description = "Comment status", example = "APPROVED") String status,
        @Schema(description = "ID of the post this comment belongs to", example = "1") Long postId,
        @Schema(description = "Username of the author of this comment", example = "johndoe") String author,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt) {}
