package com.zenith.post.domain.dtos;

import com.zenith.auth.domain.dtos.AuthorDto;
import com.zenith.category.domain.dtos.CategoryDto;
import com.zenith.post.domain.entities.PostStatus;
import com.zenith.tag.domain.dtos.TagDto;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record PostDto(
    UUID id,
    String title,
    String content,
    AuthorDto author,
    CategoryDto category,
    Set<TagDto> tags,
    Integer readingTime,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    PostStatus status) {}
