package com.zenith.controllers;

import com.zenith.dtos.requests.LoginRequest;
import com.zenith.dtos.requests.RegisterRequest;
import com.zenith.dtos.responses.AuthResponse;
import com.zenith.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid registration details")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for username: {}", request.username());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully with username: {}", request.username());
        return response;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request");
        AuthResponse response = authService.login(request);
        log.info("User logged in successfully");
        return response;
    }
}
