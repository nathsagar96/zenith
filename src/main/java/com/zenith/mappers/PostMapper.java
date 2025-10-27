package com.zenith.mappers;

import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(CreatePostRequest request);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "tags", target = "tagCount", qualifiedByName = "tagCount")
    @Mapping(source = "comments", target = "commentCount", qualifiedByName = "commentCount")
    PostResponse toResponse(Post post);

    @Named("tagCount")
    default int tagCount(Set<Tag> tags) {
        return tags != null ? tags.size() : 0;
    }

    @Named("commentCount")
    default int commentCount(List<Comment> comments) {
        return comments != null ? comments.size() : 0;
    }
}
