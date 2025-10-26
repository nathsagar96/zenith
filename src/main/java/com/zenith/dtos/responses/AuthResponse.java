package com.zenith.dtos.responses;

import java.time.LocalDateTime;

public record AuthResponse(String token, LocalDateTime expiresAt) {}
