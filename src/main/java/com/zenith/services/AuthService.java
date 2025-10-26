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
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("User with username '" + request.username() + "' already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User with email '" + request.email() + "' already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);
        String jwtToken = jwtService.generateToken(securityUser);

        LocalDateTime expiresAt = extractExpiration(jwtToken);

        return new AuthResponse(jwtToken, expiresAt);
    }

    public AuthResponse login(LoginRequest request) {
        if (request.username() == null && request.email() == null) {
            throw new ValidationException("Either username or email is required");
        }

        String username = request.username() != null ? request.username() : request.email();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(securityUser);

        LocalDateTime expiresAt = extractExpiration(jwtToken);

        return new AuthResponse(jwtToken, expiresAt);
    }

    private LocalDateTime extractExpiration(String token) {
        Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);
        if (expirationDate == null) {
            throw new IllegalStateException("JWT token does not contain expiration claim");
        }
        return expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
