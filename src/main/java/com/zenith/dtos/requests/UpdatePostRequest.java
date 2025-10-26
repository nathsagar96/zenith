package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Post update request")
public record UpdatePostRequest(
        @Size(max = 100, message = "Title must be less than {max} characters")
                @Schema(description = "Updated post title", example = "Updated Post Title")
                String title,
        @Schema(description = "Updated post content", example = "This is the updated content") String content,
        @Schema(description = "Updated list of category IDs", example = "[1, 2]") Set<Long> categoryIds,
        @Schema(description = "Updated list of tag IDs", example = "[1, 2]") Set<Long> tagIds) {}
