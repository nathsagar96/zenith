package com.zenith.tag;

import com.zenith.post.domain.entities.Post;
import com.zenith.post.domain.entities.PostStatus;
import com.zenith.tag.domain.dtos.TagDto;
import com.zenith.tag.domain.entities.Tag;
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
