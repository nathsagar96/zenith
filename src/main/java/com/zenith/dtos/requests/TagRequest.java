package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Tag creation request")
public record TagRequest(
        @NotBlank(message = "Name is required")
                @Size(max = 50, message = "Name must be less than {max} characters")
                @Schema(description = "Tag name", example = "Java", required = true)
                String name) {}
