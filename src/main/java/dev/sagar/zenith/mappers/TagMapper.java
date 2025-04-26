package dev.sagar.zenith.mappers;

import dev.sagar.zenith.domain.PostStatus;
import dev.sagar.zenith.domain.dtos.TagDto;
import dev.sagar.zenith.domain.entities.Post;
import dev.sagar.zenith.domain.entities.Tag;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
  @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
  TagDto toTagDto(Tag tag);

  List<TagDto> toTagDtoList(List<Tag> tags);

  @Named("calculatePostCount")
  default Integer calculatePostCount(Set<Post> posts) {
    if (posts == null) {
      return 0;
    }
    return (int)
        posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
  }
}
