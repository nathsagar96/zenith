package dev.sagar.zenith.domain.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequest {

  @NotEmpty(message = "Tag names cannot be empty")
  @Size(max = 10, message = "Maximum {max} tags are allowed")
  private Set<
          @Size(
              min = 2,
              max = 30,
              message = "Tag name must be between {min} and {max} characters long")
          @Pattern(
              regexp = "^[\\w\\s-]+$",
              message = "Tag name can only contain letters, numbers, spaces, and hyphens")
          String>
      names;
}
