package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object for authentication")
public record AuthResponse(
        @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                String token,
        @Schema(description = "Token expiration time in milliseconds", example = "3600000") Long expiresIn) {}
