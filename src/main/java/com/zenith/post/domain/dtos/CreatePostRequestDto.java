package com.zenith.post.domain.dtos;

import com.zenith.post.domain.entities.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record CreatePostRequestDto(
    @NotBlank(message = "Title cannot be empty")
        @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters")
        String title,
    @NotBlank(message = "Content cannot be empty")
        @Size(min = 10, message = "Content must be at least {min} characters")
        String content,
    @NotNull UUID categoryId,
    @Size(max = 10, message = "Tags cannot exceed {max} tags") Set<UUID> tagIds,
    @NotNull PostStatus status) {
  public CreatePostRequestDto(String title, String content, UUID categoryId, PostStatus status) {
    this(title, content, categoryId, new HashSet<>(), status);
  }
}
