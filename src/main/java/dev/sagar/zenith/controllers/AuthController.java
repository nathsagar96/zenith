package dev.sagar.zenith.controllers;

import dev.sagar.zenith.domain.dtos.AuthResponse;
import dev.sagar.zenith.domain.dtos.LoginRequest;
import dev.sagar.zenith.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationService authenticationService;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse login(@RequestBody LoginRequest loginRequest) {
    UserDetails userDetails =
        authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
    String token = authenticationService.generateToken(userDetails);
    return AuthResponse.builder()
        .token(token)
        .expiresIn(86400) // Set the expiration time as needed
        .build();
  }
}
