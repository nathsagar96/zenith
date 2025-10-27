package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Post creation request")
public record CreatePostRequest(
        @NotBlank(message = "Title is required")
                @Size(max = 100, message = "Title must be less than {max} characters")
                @Schema(
                        description = "Post title",
                        example = "My First Post",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String title,
        @NotBlank(message = "Content is required")
                @Schema(
                        description = "Post content",
                        example = "This is the content of my post",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String content,
        @NotBlank(message = "Category name is required")
                @Schema(
                        description = "Category name",
                        example = "Technology",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String category,
        @Size(min = 1, message = "At least {min} tag ID is required")
                @Schema(
                        description = "List of tag IDs",
                        example = "[1, 2]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                Set<Long> tagIds) {}
