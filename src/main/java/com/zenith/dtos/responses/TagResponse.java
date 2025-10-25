package com.zenith.dtos.responses;

import java.time.LocalDateTime;

public record TagResponse(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt, int postCount) {}
