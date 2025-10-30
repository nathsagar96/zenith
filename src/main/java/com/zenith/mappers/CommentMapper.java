package com.zenith.mappers;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toEntity(CreateCommentRequest request);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "author.id", target = "authorId")
    CommentResponse toResponse(Comment comment);
}
