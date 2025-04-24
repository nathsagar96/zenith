package dev.sagar.zenith.mappers;

import dev.sagar.zenith.domain.PostStatus;
import dev.sagar.zenith.domain.dtos.CategoryDto;
import dev.sagar.zenith.domain.dtos.CreateCategoryRequest;
import dev.sagar.zenith.domain.entities.Category;
import dev.sagar.zenith.domain.entities.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

  @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
  CategoryDto toDto(Category category);

  Category toEntity(CreateCategoryRequest createCategoryRequest);

  @Named("calculatePostCount")
  default long calculatePostCount(List<Post> posts) {
    if (null == posts) {
      return 0;
    }

    return posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
  }
}
