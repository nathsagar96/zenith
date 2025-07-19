package com.zenith.post.domain.dtos;

import com.zenith.post.domain.entities.PostStatus;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record UpdatePostRequest(
    UUID id, String title, String content, UUID categoryId, Set<UUID> tagIds, PostStatus status) {
  public UpdatePostRequest(
      UUID id, String title, String content, UUID categoryId, PostStatus status) {
    this(id, title, content, categoryId, new HashSet<>(), status);
  }
}
