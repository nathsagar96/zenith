package com.zenith.auth;

import com.zenith.auth.domain.dtos.AuthResponse;
import com.zenith.auth.domain.dtos.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationService authenticationService;

  public AuthController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse login(@RequestBody LoginRequest loginRequest) {
    UserDetails userDetails =
        authenticationService.authenticate(loginRequest.email(), loginRequest.password());
    String token = authenticationService.generateToken(userDetails);
    return new AuthResponse(token, 86400);
  }
}
