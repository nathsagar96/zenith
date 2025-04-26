package dev.sagar.zenith.mappers;

import dev.sagar.zenith.domain.CreatePostRequest;
import dev.sagar.zenith.domain.UpdatePostRequest;
import dev.sagar.zenith.domain.dtos.CreatePostRequestDto;
import dev.sagar.zenith.domain.dtos.PostDto;
import dev.sagar.zenith.domain.dtos.UpdatePostRequestDto;
import dev.sagar.zenith.domain.entities.Post;
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
