package com.zenith.dtos.responses;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String bio,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int postCount,
        int commentCount) {}
