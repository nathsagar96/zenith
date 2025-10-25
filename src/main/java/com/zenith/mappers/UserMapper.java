package com.zenith.mappers;

import com.zenith.dtos.requests.CreateUserRequest;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "comments", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(source = "posts", target = "postCount", qualifiedByName = "postCount")
    @Mapping(source = "comments", target = "commentCount", qualifiedByName = "commentCount")
    UserResponse toResponse(User user);

    @Named("postCount")
    default int postCount(List<Post> posts) {
        return posts != null ? posts.size() : 0;
    }

    @Named("commentCount")
    default int commentCount(List<Comment> comments) {
        return comments != null ? comments.size() : 0;
    }
}
