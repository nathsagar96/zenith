package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Request object for updating a post")
public record UpdatePostRequest(
        @Schema(description = "Updated title of the post", example = "Updated Title")
                @Size(max = 100, message = "Title must be less than {max} characters")
                String title,
        @Schema(description = "Updated content of the post", example = "This is the updated content") String content,
        @Schema(description = "Updated category ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID categoryId,
        @Schema(description = "Updated list of tags", example = "[\"spring\", \"java\"]") Set<String> tags) {}
