package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Tag response")
public record TagResponse(
        @Schema(description = "Tag ID", example = "1") Long id,
        @Schema(description = "Tag name", example = "Java") String name,
        @Schema(description = "Creation timestamp", example = "2023-01-01T00:00:00") LocalDateTime createdAt,
        @Schema(description = "Last update timestamp", example = "2023-01-01T00:00:00") LocalDateTime updatedAt,
        @Schema(description = "Number of posts associated with this tag", example = "10") int postCount) {}
