package com.zenith.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(@NotBlank(message = "Content is required") String content) {}
