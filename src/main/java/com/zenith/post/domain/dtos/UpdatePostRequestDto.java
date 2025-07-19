package com.zenith.post.domain.dtos;

import com.zenith.post.domain.entities.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record UpdatePostRequestDto(
    @NotNull(message = "Post ID cannot be null") UUID id,
    @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters long")
        String title,
    @NotBlank(message = "Content cannot be blank")
        @Size(min = 10, message = "Content must be at least {min} characters long")
        String content,
    @NotNull(message = "Category ID cannot be null") UUID categoryId,
    @Size(max = 10, message = "Maximum {max} tags are allowed") Set<UUID> tagIds,
    @NotNull(message = "Status cannot be null") PostStatus status) {
  public UpdatePostRequestDto(
      UUID id, String title, String content, UUID categoryId, PostStatus status) {
    this(id, title, content, categoryId, new HashSet<>(), status);
  }
}
