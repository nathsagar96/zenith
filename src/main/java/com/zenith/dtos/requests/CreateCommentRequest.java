package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Comment creation request")
public record CreateCommentRequest(
        @NotBlank(message = "Content is required")
                @Schema(
                        description = "Comment content",
                        example = "This is a great post!",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String content,
        @NotNull(message = "Post ID is required")
                @Schema(
                        description = "ID of the post this comment belongs to",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                Long postId) {}
