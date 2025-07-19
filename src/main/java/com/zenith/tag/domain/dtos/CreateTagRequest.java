package com.zenith.tag.domain.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateTagRequest(
    @NotEmpty(message = "Tag names cannot be empty")
        @Size(max = 10, message = "Maximum {max} tags are allowed")
        Set<
                @Size(
                    min = 2,
                    max = 30,
                    message = "Tag name must be between {min} and {max} characters long")
                @Pattern(
                    regexp = "^[\\w\\s-]+$",
                    message = "Tag name can only contain letters, numbers, spaces, and hyphens")
                String>
            names) {}
