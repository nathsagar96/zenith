package com.zenith.auth.impl;

import com.zenith.auth.UserRepository;
import com.zenith.auth.UserService;
import com.zenith.auth.domain.entities.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getUserById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
  }
}
