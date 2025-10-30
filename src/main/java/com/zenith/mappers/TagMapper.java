package com.zenith.mappers;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "posts", ignore = true)
    Tag toEntity(TagRequest request);

    @Mapping(source = "id", target = "tagId")
    @Mapping(source = "posts", target = "postCount", qualifiedByName = "postCount")
    TagResponse toResponse(Tag tag);

    @Named("postCount")
    default int postCount(List<Post> posts) {
        return posts != null ? posts.size() : 0;
    }
}
