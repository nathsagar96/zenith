package dev.sagar.zenith.domain.dtos;

import dev.sagar.zenith.domain.PostStatus;
import java.time.LocalDateTime;
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
public class PostDto {
  private UUID id;
  private String title;
  private String content;
  private AuthorDto author;
  private CategoryDto category;
  private Set<TagDto> tags;
  private Integer readingTime;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private PostStatus status;
}
