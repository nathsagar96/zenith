package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for creating or updating a tag")
public record TagRequest(
        @Schema(description = "Name of the tag", example = "Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "Name is required")
                @Size(max = 50, message = "Name must be less than {max} characters")
                String name) {}
