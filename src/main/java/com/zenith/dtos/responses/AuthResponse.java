package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response")
public record AuthResponse(
        @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token,
        @Schema(description = "Token expiration time in milliseconds", example = "1672531199000") Long expiresIn) {}
