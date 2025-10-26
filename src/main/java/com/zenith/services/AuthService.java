package com.zenith.services;

import com.zenith.dtos.requests.LoginRequest;
import com.zenith.dtos.requests.RegisterRequest;
import com.zenith.dtos.requests.ResetPasswordRequest;
import com.zenith.dtos.responses.AuthResponse;
import com.zenith.entities.User;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.repositories.UserRepository;
import com.zenith.security.JwtService;
import com.zenith.security.SecurityUser;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username() != null ? request.username() : request.email(), request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(securityUser);

        LocalDateTime expiresAt = extractExpiration(jwtToken);

        return new AuthResponse(jwtToken, expiresAt);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String usernameOrEmail =
                SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository
                .findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with username or email: " + usernameOrEmail));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ValidationException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private LocalDateTime extractExpiration(String token) {
        return jwtService
                .extractClaim(token, Claims::getExpiration)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
