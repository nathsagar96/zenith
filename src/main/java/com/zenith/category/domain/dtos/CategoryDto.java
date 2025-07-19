package com.zenith.category.domain.dtos;

import java.util.UUID;

public record CategoryDto(UUID id, String name, long postCount) {}
