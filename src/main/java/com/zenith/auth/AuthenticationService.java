package com.zenith.auth;

import com.zenith.auth.domain.dtos.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {

  UserDetails authenticate(String username, String password);

  String generateToken(UserDetails userDetails);

  UserDetails validateToken(String token);

  UserDetails register(RegisterRequest request);
}
