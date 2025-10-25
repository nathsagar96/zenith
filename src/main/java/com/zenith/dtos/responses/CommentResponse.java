package com.zenith.dtos.responses;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String status,
        Long postId,
        Long authorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
