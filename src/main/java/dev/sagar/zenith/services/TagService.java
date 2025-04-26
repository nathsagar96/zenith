package dev.sagar.zenith.services;

import dev.sagar.zenith.domain.entities.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {
  List<Tag> getTags();

  List<Tag> createTags(
      @NotEmpty(message = "Tag names cannot be empty")
          @Size(max = 10, message = "Maximum {max} tags are allowed")
          Set<
                  @Size(
                      min = 2,
                      max = 30,
                      message = "Tag name must be between {min} and {max} characters long")
                  @Pattern(
                      regexp = "^[\\w\\s-]+$",
                      message = "Tag name can only contain letters, numbers, spaces, and hyphens")
                  String>
              names);

  void deleteTag(UUID id);

  Tag getTagById(UUID id);

  List<Tag> getTagsByIds(Set<UUID> ids);
}
