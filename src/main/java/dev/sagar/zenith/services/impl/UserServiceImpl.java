package dev.sagar.zenith.services.impl;

import dev.sagar.zenith.domain.entities.User;
import dev.sagar.zenith.repositories.UserRepository;
import dev.sagar.zenith.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User getUserById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
  }
}
