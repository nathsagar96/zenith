package com.zenith.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Name is required") @Size(max = 50, message = "Name must be less than {max} characters")
                String name) {}
