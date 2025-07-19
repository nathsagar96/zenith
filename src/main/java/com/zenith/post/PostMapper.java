package com.zenith.post;

import com.zenith.post.domain.dtos.*;
import com.zenith.post.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

  @Mapping(target = "author", source = "author")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "tags", source = "tags")
  @Mapping(target = "status", source = "status")
  PostDto toDto(Post post);

  CreatePostRequest toCreatePostRequest(CreatePostRequestDto createPostRequestDto);

  UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto updatePostRequestDto);
}
