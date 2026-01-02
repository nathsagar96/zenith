package com.zenith.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    private Authentication authentication;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private SecurityUser securityUser;
    private String jwtToken;
    private Long expiration;

    @BeforeEach
    void setUp() {
        // Setup common test data
        registerRequest = new RegisterRequest("testuser", "test@example.com", "password123");
        loginRequest = new LoginRequest("testuser", null, "password123");
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        securityUser = new SecurityUser(user);
        jwtToken = "test.jwt.token";
        expiration = 3600000L;
    }

    @Test
    @DisplayName("should register user successfully when username and email are unique")
    void shouldRegisterUserSuccessfullyWhenUsernameAndEmailAreUnique() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(SecurityUser.class))).thenReturn(jwtToken);
        when(jwtService.getJwtExpiration()).thenReturn(expiration);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(jwtToken);
        assertThat(response.expiresIn()).isEqualTo(expiration);
        verify(userRepository, times(1)).existsByUsername(registerRequest.username());
        verify(userRepository, times(1)).existsByEmail(registerRequest.email());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(SecurityUser.class));
    }

    @Test
    @DisplayName("should throw duplicate resource exception when username already exists")
    void shouldThrowDuplicateResourceExceptionWhenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));

        verify(userRepository, times(1)).existsByUsername(registerRequest.username());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should throw duplicate resource exception when email already exists")
    void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));

        verify(userRepository, times(1)).existsByUsername(registerRequest.username());
        verify(userRepository, times(1)).existsByEmail(registerRequest.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should login user successfully with username")
    void shouldLoginUserSuccessfullyWithUsername() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(jwtService.generateToken(securityUser)).thenReturn(jwtToken);
        when(jwtService.getJwtExpiration()).thenReturn(expiration);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(jwtToken);
        assertThat(response.expiresIn()).isEqualTo(expiration);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(securityUser);
    }

    @Test
    @DisplayName("should login user successfully with email")
    void shouldLoginUserSuccessfullyWithEmail() {
        // Arrange
        LoginRequest emailLoginRequest = new LoginRequest(null, "test@example.com", "password123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(jwtService.generateToken(securityUser)).thenReturn(jwtToken);
        when(jwtService.getJwtExpiration()).thenReturn(expiration);

        // Act
        AuthResponse response = authService.login(emailLoginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(jwtToken);
        assertThat(response.expiresIn()).isEqualTo(expiration);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(securityUser);
    }

    @Test
    @DisplayName("should throw validation exception when both username and email are null")
    void shouldThrowValidationExceptionWhenBothUsernameAndEmailAreNull() {
        // Arrange
        LoginRequest invalidLoginRequest = new LoginRequest(null, null, "password123");

        // Act & Assert
        assertThrows(ValidationException.class, () -> authService.login(invalidLoginRequest));

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("should handle authentication failure gracefully")
    void shouldHandleAuthenticationFailureGracefully() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }
}
