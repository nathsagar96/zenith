package com.zenith.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotBlank(message = "Content is required") String content,
        @NotNull(message = "Post ID is required") Long postId,
        @NotNull(message = "Author ID is required") Long authorId,
        String status) {}
