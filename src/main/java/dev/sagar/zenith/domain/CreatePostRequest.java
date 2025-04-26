package dev.sagar.zenith.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
  private String title;
  private String content;
  private UUID categoryId;
  @Builder.Default private Set<UUID> tagIds = new HashSet<>();
  private PostStatus status;
}
