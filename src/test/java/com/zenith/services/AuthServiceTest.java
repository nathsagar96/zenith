package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.LoginRequest;
import com.zenith.dtos.requests.RegisterRequest;
import com.zenith.dtos.responses.AuthResponse;
import com.zenith.entities.User;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ValidationException;
import com.zenith.repositories.UserRepository;
import com.zenith.security.JwtService;
import com.zenith.security.SecurityUser;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private SecurityUser securityUser;
    private String jwtToken;
    private LocalDateTime expiration;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("testuser", "test@example.com", "password");
        loginRequest = new LoginRequest("testuser", null, "password");
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        user.setId(1L);
        securityUser = new SecurityUser(user);
        jwtToken = "test-jwt-token";
        expiration = LocalDateTime.now().plusHours(1);
    }

    @Test
    @DisplayName("should register user successfully")
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(SecurityUser.class))).thenReturn(jwtToken);
        when(jwtService.extractClaim(eq(jwtToken), any()))
                .thenReturn(Date.from(
                        expiration.atZone(java.time.ZoneId.systemDefault()).toInstant()));
        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        assertEquals(expiration.withNano(0), response.expiresAt().withNano(0));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when username already exists")
    void shouldThrowDuplicateResourceExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when email already exists")
    void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should login user successfully")
    void shouldLoginUserSuccessfully() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(jwtService.generateToken(any(SecurityUser.class))).thenReturn(jwtToken);
        when(jwtService.extractClaim(eq(jwtToken), any()))
                .thenReturn(Date.from(
                        expiration.atZone(java.time.ZoneId.systemDefault()).toInstant()));

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.token());
        assertEquals(expiration.withNano(0), response.expiresAt().withNano(0));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("should throw ValidationException when login credentials are invalid")
    void shouldThrowValidationExceptionWhenLoginCredentialsAreInvalid() {
        LoginRequest invalidRequest = new LoginRequest(null, null, "password");

        assertThrows(ValidationException.class, () -> authService.login(invalidRequest));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
