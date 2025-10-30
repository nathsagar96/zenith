package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for creating a comment")
public record CreateCommentRequest(
        @Schema(
                        description = "Content of the comment",
                        example = "This is a great post!",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Content is required")
                String content) {}
