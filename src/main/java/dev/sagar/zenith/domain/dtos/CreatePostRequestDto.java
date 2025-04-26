package dev.sagar.zenith.domain.dtos;

import dev.sagar.zenith.domain.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreatePostRequestDto {
  @NotBlank(message = "Title cannot be empty")
  @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters")
  private String title;

  @NotBlank(message = "Content cannot be empty")
  @Size(min = 10, message = "Content must be at least {min} characters")
  private String content;

  @NotNull private UUID categoryId;

  @Builder.Default
  @Size(max = 10, message = "Tags cannot exceed {max} tags")
  private Set<UUID> tagIds = new HashSet<>();

  @NotNull private PostStatus status;
}
