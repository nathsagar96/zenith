package com.zenith.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Authentication response")
public record AuthResponse(
        @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token,
        @Schema(description = "Token expiration time", example = "2023-12-31T23:59:59") LocalDateTime expiresAt) {}
