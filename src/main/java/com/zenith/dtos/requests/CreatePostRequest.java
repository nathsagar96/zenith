package com.zenith.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreatePostRequest(
        @NotBlank(message = "Title is required") @Size(max = 100, message = "Title must be less than {max} characters")
                String title,
        @NotBlank(message = "Content is required") String content,
        @NotNull(message = "Author ID is required") Long authorId,
        @Size(min = 1, message = "At least {min} category ID is required") Set<Long> categoryIds,
        @Size(min = 1, message = "At least {min} tag ID is required") Set<Long> tagIds) {}
