package com.zenith.auth.impl;

import com.zenith.auth.AuthenticationService;
import com.zenith.auth.UserRepository;
import com.zenith.auth.domain.dtos.RegisterRequest;
import com.zenith.auth.domain.entities.User;
import com.zenith.common.exceptions.UserAlreadyExistsException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final long jwtExpiryMs;
  private final String secretKey;

  public AuthenticationServiceImpl(
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      @Value("${jwt.expiration:86400000}") long jwtExpiryMs,
      @Value("${jwt.secret}") String secretKey) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtExpiryMs = jwtExpiryMs;
    this.secretKey = secretKey;
  }

  @Override
  public UserDetails authenticate(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    return userDetailsService.loadUserByUsername(username);
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiryMs))
        .signWith(getSigningKey())
        .compact();
  }

  @Override
  public UserDetails validateToken(String token) {
    String username = extractUsername(token);
    return userDetailsService.loadUserByUsername(username);
  }

  @Override
  public UserDetails register(RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new UserAlreadyExistsException(
          "User with email " + request.email() + " already exists");
    }

    User user =
        new User(request.name(), request.email(), passwordEncoder.encode(request.password()));
    userRepository.save(user);
    return userDetailsService.loadUserByUsername(user.getEmail());
  }

  private String extractUsername(String token) {
    return Jwts.parser()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  private Key getSigningKey() {
    byte[] keyBytes = secretKey.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
