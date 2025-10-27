package com.zenith.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Category creation request")
public record CategoryRequest(
        @NotBlank(message = "Name is required")
                @Size(max = 50, message = "Name must be less than {max} characters")
                @Schema(
                        description = "Category name",
                        example = "Technology",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String name) {}
