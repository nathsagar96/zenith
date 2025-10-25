package com.zenith.dtos.requests;

import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdatePostRequest(
        @Size(max = 100, message = "Title must be less than {max} characters") String title,
        String content,
        Set<Long> categoryIds,
        Set<Long> tagIds) {}
