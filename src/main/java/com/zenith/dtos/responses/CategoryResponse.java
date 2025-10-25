package com.zenith.dtos.responses;

import java.time.LocalDateTime;

public record CategoryResponse(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt, int postCount) {}
