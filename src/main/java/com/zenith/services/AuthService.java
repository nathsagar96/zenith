package com.zenith.services;

import com.zenith.dtos.requests.LoginRequest;
import com.zenith.dtos.requests.RegisterRequest;
import com.zenith.dtos.responses.AuthResponse;
import com.zenith.entities.User;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ValidationException;
import com.zenith.repositories.UserRepository;
import com.zenith.security.JwtService;
import com.zenith.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with username: {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: Username '{}' already exists", request.username());
            throw new DuplicateResourceException("User with username '" + request.username() + "' already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email '{}' already exists", request.email());
            throw new DuplicateResourceException("User with email '" + request.email() + "' already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
        log.info("User registered successfully with username: {}", request.username());

        SecurityUser securityUser = new SecurityUser(user);
        String jwtToken = jwtService.generateToken(securityUser);

        Long expiresIn = jwtService.getJwtExpiration();

        return new AuthResponse(jwtToken, expiresIn);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user");

        if (request.username() == null && request.email() == null) {
            log.warn("Login failed: Neither username nor email provided");
            throw new ValidationException("Either username or email is required");
        }

        String username = request.username() != null ? request.username() : request.email();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(securityUser);

        log.info("User logged in successfully: {}", username);

        Long expiresIn = jwtService.getJwtExpiration();

        return new AuthResponse(jwtToken, expiresIn);
    }
}
