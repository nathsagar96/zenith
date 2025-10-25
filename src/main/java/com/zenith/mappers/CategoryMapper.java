package com.zenith.mappers;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.entities.Category;
import com.zenith.entities.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "posts", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(source = "posts", target = "postCount", qualifiedByName = "postCount")
    CategoryResponse toResponse(Category category);

    @Named("postCount")
    default int postCount(List<Post> posts) {
        return posts != null ? posts.size() : 0;
    }
}
