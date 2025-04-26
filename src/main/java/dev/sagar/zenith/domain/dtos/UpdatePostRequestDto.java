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
public class UpdatePostRequestDto {

  @NotNull(message = "Post ID cannot be null")
  private UUID id;

  @NotBlank(message = "Title cannot be blank")
  @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters long")
  private String title;

  @NotBlank(message = "Content cannot be blank")
  @Size(min = 10, message = "Content must be at least {min} characters long")
  private String content;

  @NotNull(message = "Category ID cannot be null")
  private UUID categoryId;

  @Builder.Default
  @Size(max = 10, message = "Maximum {max} tags are allowed")
  private Set<UUID> tagIds = new HashSet<>();

  @NotNull(message = "Status cannot be null")
  private PostStatus status;
}
