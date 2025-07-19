package com.zenith.auth.domain.dtos;

public record AuthResponse(String token, long expiresIn) {}
