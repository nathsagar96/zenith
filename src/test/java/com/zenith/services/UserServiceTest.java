package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.CreateUserRequest;
import com.zenith.dtos.requests.UpdateUserRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.entities.User;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.UnauthorizedException;
import com.zenith.mappers.UserMapper;
import com.zenith.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(RoleType.USER);

        userResponse = new UserResponse(
                1L, "testuser", "test@example.com", "First", "Last", "Bio", RoleType.USER.name(), null, null, 1, 1);

        createUserRequest = new CreateUserRequest(
                "testuser", "test@example.com", "password", "First", "Last", "Bio", RoleType.USER.name());
        updateUserRequest = new UpdateUserRequest(
                "newUsername", "newEmail@example.com", "newpassword", "NewFirst", "NewLast", "NewBio");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), "password", List.of());
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("should get all users")
    void shouldGetAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        PageResponse<UserResponse> response = userService.getAllUser(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(userResponse, response.getContent().getFirst());

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("should get all users by role")
    void shouldGetAllUsersByRole() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findByRole(any(RoleType.class), any(Pageable.class)))
                .thenReturn(userPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        PageResponse<UserResponse> response = userService.getAllUserByRole("USER", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(userResponse, response.getContent().getFirst());

        verify(userRepository, times(1)).findByRole(RoleType.USER, pageable);
    }

    @Test
    @DisplayName("should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(userResponse, response);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when user not found by id")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should get user by email successfully")
    void shouldGetUserByEmailSuccessfully() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse response = userService.getUserByEmail("test@example.com");

        assertNotNull(response);
        assertEquals(userResponse, response);

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when user not found by email")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("test@example.com"));

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("should create user successfully")
    void shouldCreateUserSuccessfully() {
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(user);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse response = userService.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(userResponse, response);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when username already exists")
    void shouldThrowDuplicateResourceExceptionWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createUserRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when email already exists")
    void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(createUserRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should update user successfully")
    void shouldUpdateUserSuccessfully() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse response = userService.updateUser(1L, updateUserRequest);

        assertNotNull(response);
        assertEquals(userResponse, response);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw ForbiddenException when user tries to update another user")
    void shouldThrowForbiddenExceptionWhenUserTriesToUpdateAnotherUser() {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setRole(RoleType.USER);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(differentUser));

        assertThrows(ForbiddenException.class, () -> userService.updateUser(1L, updateUserRequest));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when username already exists for update")
    void shouldThrowDuplicateResourceExceptionWhenUsernameAlreadyExistsForUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        UpdateUserRequest requestWithNewUsername = new UpdateUserRequest(
                "newUsername", "newemail@example.com", "password", "NewFirst", "NewLast", "NewBio");

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(1L, requestWithNewUsername));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when email already exists for update")
    void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExistsForUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        UpdateUserRequest requestWithNewEmail = new UpdateUserRequest(
                "newUsername", "newemail@example.com", "password", "NewFirst", "NewLast", "NewBio");

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(1L, requestWithNewEmail));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when user not found for deletion")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundForDeletion() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.updateUserRole(1L, RoleType.ADMIN));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when user not found for role update")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundForRoleUpdate() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserRole(1L, RoleType.ADMIN));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should get current user successfully")
    void shouldGetCurrentUserSuccessfully() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("should throw UnauthorizedException when no authenticated user found")
    void shouldThrowUnauthorizedExceptionWhenNoAuthenticatedUserFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());

        verify(userRepository, times(1)).findByUsername("testuser");
    }
}
