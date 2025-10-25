package com.zenith.dtos.requests;

import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdatePostRequest(
        @Size(max = 100, message = "Title must be less than {max} characters") String title,
        String content,
        String status,
        @Size(min = 1, message = "At least {min} category ID required") Set<Long> categoryIds,
        @Size(min = 1, message = "At least {min} tag ID required") Set<Long> tagIds) {}
