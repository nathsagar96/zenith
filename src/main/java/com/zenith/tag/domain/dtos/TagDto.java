package com.zenith.tag.domain.dtos;

import java.util.UUID;

public record TagDto(UUID id, String name, Integer postCount) {}
