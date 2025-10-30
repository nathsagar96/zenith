package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Request object for creating a post")
public record CreatePostRequest(
        @Schema(
                        description = "Title of the post",
                        example = "Getting Started with Spring Boot",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Title is required")
                @Size(max = 100, message = "Title must be less than {max} characters")
                String title,
        @Schema(
                        description = "Content of the post",
                        example = "This is the content of the post",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Content is required")
                String content,
        @Schema(
                        description = "ID of the category",
                        example = "123e4567-e89b-12d3-a456-426614174000",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "Category ID is required")
                UUID categoryId,
        @Schema(
                        description = "List of tags",
                        example = "[\"spring\", \"java\"]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @Size(min = 1, message = "At least {min} tag is required")
                Set<String> tags) {}
