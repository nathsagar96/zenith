package com.zenith.auth;

import com.zenith.auth.domain.entities.User;
import java.util.UUID;

public interface UserService {
  User getUserById(UUID id);
}
