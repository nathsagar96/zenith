package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Comment update request")
public record UpdateCommentRequest(
        @NotBlank(message = "Content is required")
                @Schema(
                        description = "Updated comment content",
                        example = "This is an updated comment",
                        required = true)
                String content) {}
