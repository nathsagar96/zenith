package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Post creation request")
public record CreatePostRequest(
        @NotBlank(message = "Title is required")
                @Size(max = 100, message = "Title must be less than {max} characters")
                @Schema(description = "Post title", example = "My First Post", required = true)
                String title,
        @NotBlank(message = "Content is required")
                @Schema(description = "Post content", example = "This is the content of my post", required = true)
                String content,
        @Size(min = 1, message = "At least {min} category ID is required")
                @Schema(description = "List of category IDs", example = "[1, 2]", required = true)
                Set<Long> categoryIds,
        @Size(min = 1, message = "At least {min} tag ID is required")
                @Schema(description = "List of tag IDs", example = "[1, 2]", required = true)
                Set<Long> tagIds) {}
