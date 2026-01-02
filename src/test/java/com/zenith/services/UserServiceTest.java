package com.zenith.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.UpdateUserRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.entities.User;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.UnauthorizedException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.UserMapper;
import com.zenith.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    private UUID userId;
    private Pageable pageable;
    private String username;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup common test data
        username = "testuser";
        userId = UUID.randomUUID();
        user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(RoleType.USER)
                .build();
        userResponse = new UserResponse(
                userId, username, "test@example.com", "Test", "User", "Test Bio", RoleType.USER, null, null, 0, 0);
        pageable = PageRequest.of(0, 10);
        updateRequest =
                new UpdateUserRequest("newusername", "new@example.com", "newpassword", "New", "User", "New Bio");
    }

    @Test
    @DisplayName("should validate sort params successfully when valid")
    void shouldValidateSortParamsSuccessfullyWhenValid() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            userService.validateSortParams("username", "asc");
            userService.validateSortParams("email", "desc");
            userService.validateSortParams("firstname", "asc");
            userService.validateSortParams("lastname", "desc");
            userService.validateSortParams("createdat", "asc");
            userService.validateSortParams("updatedat", "desc");
        });
    }

    @Test
    @DisplayName("should throw validation exception when sort field is invalid")
    void shouldThrowValidationExceptionWhenSortFieldIsInvalid() {
        // Act & Assert
        ValidationException exception =
                assertThrows(ValidationException.class, () -> userService.validateSortParams("invalidField", "asc"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort field: invalidField");
    }

    @Test
    @DisplayName("should throw validation exception when sort direction is invalid")
    void shouldThrowValidationExceptionWhenSortDirectionIsInvalid() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> userService.validateSortParams("username", "invalidDirection"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort direction: invalidDirection");
    }

    @Test
    @DisplayName("should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Arrange
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        PageResponse<UserResponse> result = userService.getAllUsers(null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(userResponse);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    @DisplayName("should get all users by role successfully")
    void shouldGetAllUsersByRoleSuccessfully() {
        // Arrange
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findByRole(RoleType.ADMIN, pageable)).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        PageResponse<UserResponse> result = userService.getAllUsers(RoleType.ADMIN, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(userResponse);

        verify(userRepository, times(1)).findByRole(RoleType.ADMIN, pageable);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    @DisplayName("should get current user successfully")
    void shouldGetCurrentUserSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.getCurrentUser(username);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userResponse);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    @DisplayName("should throw unauthorized exception when current user not found")
    void shouldThrowUnauthorizedExceptionWhenCurrentUserNotFound() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser(username));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.getUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userResponse);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    @DisplayName("should throw resource not found exception when user not found")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("should update user successfully when user updates own profile")
    void shouldUpdateUserSuccessfullyWhenUserUpdatesOwnProfile() {
        // Arrange
        User currentUser = User.builder()
                .username(username)
                .email("test@example.com")
                .password("encodedPassword")
                .role(RoleType.USER)
                .build();
        currentUser.setId(userId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userRepository.existsByUsername(updateRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(updateRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(updateRequest.password())).thenReturn("encodedNewPassword");
        when(userRepository.save(currentUser)).thenReturn(currentUser);
        when(userMapper.toResponse(currentUser)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.updateUser(username, userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userResponse);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername(updateRequest.username());
        verify(userRepository, times(1)).existsByEmail(updateRequest.email());
        verify(passwordEncoder, times(1)).encode(updateRequest.password());
        verify(userRepository, times(1)).save(currentUser);
        verify(userMapper, times(1)).toResponse(currentUser);
    }

    @Test
    @DisplayName("should update user successfully when admin updates other user")
    void shouldUpdateUserSuccessfullyWhenAdminUpdatesOtherUser() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        User adminUser = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("encodedPassword")
                .role(RoleType.ADMIN)
                .build();
        adminUser.setId(UUID.randomUUID());

        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("encodedPassword")
                .role(RoleType.USER)
                .build();
        otherUser.setId(otherUserId);

        UpdateUserRequest adminUpdateRequest =
                new UpdateUserRequest(null, null, null, "Updated", "Name", "Updated Bio");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
        when(userRepository.save(otherUser)).thenReturn(otherUser);
        when(userMapper.toResponse(otherUser)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.updateUser("admin", otherUserId, adminUpdateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userResponse);

        verify(userRepository, times(1)).findByUsername("admin");
        verify(userRepository, times(1)).findById(otherUserId);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(otherUser);
        verify(userMapper, times(1)).toResponse(otherUser);
    }

    @Test
    @DisplayName("should throw forbidden exception when non admin user updates other user")
    void shouldThrowForbiddenExceptionWhenNonAdminUserUpdatesOtherUser() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        User currentUser = User.builder()
                .username(username)
                .email("test@example.com")
                .password("encodedPassword")
                .role(RoleType.USER)
                .build();
        currentUser.setId(userId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(currentUser));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> userService.updateUser(username, otherUserId, updateRequest));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw duplicate resource exception when updating user with duplicate username")
    void shouldThrowDuplicateResourceExceptionWhenUpdatingUserWithDuplicateUsername() {
        // Arrange
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setId(userId);
        when(userRepository.existsByUsername(updateRequest.username())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.updateUser(username, userId, updateRequest));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername(updateRequest.username());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw duplicate resource exception when updating user with duplicate email")
    void shouldThrowDuplicateResourceExceptionWhenUpdatingUserWithDuplicateEmail() {
        // Arrange
        UpdateUserRequest emailUpdateRequest =
                new UpdateUserRequest(null, "duplicate@example.com", null, null, null, null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        user.setId(userId);
        when(userRepository.existsByEmail(emailUpdateRequest.email())).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class, () -> userService.updateUser(username, userId, emailUpdateRequest));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, times(1)).existsByEmail(emailUpdateRequest.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.updateUserRole(userId, RoleType.ADMIN);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userResponse);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    @DisplayName("should throw resource not found exception when updating role for non existent user")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingRoleForNonExistentUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserRole(userId, RoleType.ADMIN));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("should throw resource not found exception when deleting non existent user")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentUser() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }
}
