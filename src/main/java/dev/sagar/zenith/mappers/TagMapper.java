package dev.sagar.zenith.mappers;

import dev.sagar.zenith.domain.PostStatus;
import dev.sagar.zenith.domain.dtos.TagResponse;
import dev.sagar.zenith.domain.entities.Post;
import dev.sagar.zenith.domain.entities.Tag;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

  @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
  TagResponse toTagResponse(Tag tag);

  @Named("calculatePostCount")
  default long calculatePostCount(List<Post> posts) {
    if (posts == null) {
      return 0;
    }

    return posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
  }
}
