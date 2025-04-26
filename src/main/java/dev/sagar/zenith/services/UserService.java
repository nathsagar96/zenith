package dev.sagar.zenith.services;

import dev.sagar.zenith.domain.entities.User;

import java.util.UUID;

public interface UserService {
  User getUserById(UUID id);
}
