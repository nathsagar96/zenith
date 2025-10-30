package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for updating a comment")
public record UpdateCommentRequest(
        @Schema(
                        description = "Updated content of the comment",
                        example = "This is an updated comment",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Content is required")
                String content) {}
