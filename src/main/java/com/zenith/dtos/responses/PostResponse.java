package com.zenith.dtos.responses;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String slug,
        String content,
        String status,
        Long authorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int categoryCount,
        int tagCount,
        int commentCount) {}
