package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.User;
import com.zenith.enums.RoleType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class UserRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should find users by role")
    void shouldFindUsersByRole() {
        // Arrange
        User adminUser = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("password")
                .role(RoleType.ADMIN)
                .build();

        User moderatorUser = User.builder()
                .username("moderator")
                .email("moderator@example.com")
                .password("password")
                .role(RoleType.MODERATOR)
                .build();

        User regularUser = User.builder()
                .username("user")
                .email("user@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        userRepository.saveAll(List.of(adminUser, moderatorUser, regularUser));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> result = userRepository.findByRole(RoleType.ADMIN, pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getRole()).isEqualTo(RoleType.ADMIN);
    }

    @Test
    @DisplayName("should return empty page when no users found by role")
    void shouldReturnEmptyPageWhenNoUsersFoundByRole() {
        // Arrange
        User regularUser = User.builder()
                .username("user")
                .email("user@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        userRepository.save(regularUser);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> result = userRepository.findByRole(RoleType.ADMIN, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return true when username exists")
    void shouldReturnTrueWhenUsernameExists() {
        // Arrange
        String username = "testuser";
        User user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByUsername(username);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when username does not exist")
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        // Arrange
        String nonExistentUsername = "nonexistent";

        // Act
        boolean result = userRepository.existsByUsername(nonExistentUsername);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return true when email exists")
    void shouldReturnTrueWhenEmailExists() {
        // Arrange
        String email = "test@example.com";
        User user = User.builder()
                .username("testuser")
                .email(email)
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByEmail(email);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when email does not exist")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";

        // Act
        boolean result = userRepository.existsByEmail(nonExistentEmail);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should find user by email")
    void shouldFindUserByEmail() {
        // Arrange
        String email = "test@example.com";
        User user = User.builder()
                .username("testuser")
                .email(email)
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByEmail(email);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";

        // Act
        Optional<User> result = userRepository.findByEmail(nonExistentEmail);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find user by username")
    void shouldFindUserByUsername() {
        // Arrange
        String username = "testuser";
        User user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByUsername(username);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("should return empty when user not found by username")
    void shouldReturnEmptyWhenUserNotFoundByUsername() {
        // Arrange
        String nonExistentUsername = "nonexistent";

        // Act
        Optional<User> result = userRepository.findByUsername(nonExistentUsername);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return true when username exists case sensitive")
    void shouldReturnTrueWhenUsernameExistsCaseSensitive() {
        // Arrange
        String username = "TestUser";
        User user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByUsername(username);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when username exists with different case")
    void shouldReturnFalseWhenUsernameExistsWithDifferentCase() {
        // Arrange
        String username = "TestUser";
        User user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByUsername("testuser");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return true when email exists case sensitive")
    void shouldReturnTrueWhenEmailExistsCaseSensitive() {
        // Arrange
        String email = "Test@Example.com";
        User user = User.builder()
                .username("testuser")
                .email(email)
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByEmail(email);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when email exists with different case")
    void shouldReturnFalseWhenEmailExistsWithDifferentCase() {
        // Arrange
        String email = "Test@Example.com";
        User user = User.builder()
                .username("testuser")
                .email(email)
                .password("password")
                .role(RoleType.USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByEmail("test@example.com");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should save and find user")
    void shouldSaveAndFindUser() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .firstName("John")
                .lastName("Doe")
                .bio("Test bio")
                .build();

        // Act
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.getFirstName()).isEqualTo("John");
        assertThat(foundUser.getLastName()).isEqualTo("Doe");
        assertThat(foundUser.getBio()).isEqualTo("Test bio");
        assertThat(foundUser.getRole()).isEqualTo(RoleType.USER);
    }

    @Test
    @DisplayName("should delete user by id")
    void shouldDeleteUserById() {
        // Arrange
        User user = User.builder()
                .username("user_to_delete")
                .email("delete@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        User savedUser = userRepository.save(user);

        // Act
        userRepository.deleteById(savedUser.getId());

        // Assert
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("should update user role")
    void shouldUpdateUserRole() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        User savedUser = userRepository.save(user);

        // Act
        savedUser.setRole(RoleType.ADMIN);
        User updatedUser = userRepository.save(savedUser);

        // Assert
        assertThat(updatedUser.getRole()).isEqualTo(RoleType.ADMIN);
    }

    @Test
    @DisplayName("should find all users")
    void shouldFindAllUsers() {
        // Arrange
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("should find users by role with multiple users")
    void shouldFindUsersByRoleWithMultipleUsers() {
        // Arrange
        User adminUser1 = User.builder()
                .username("admin1")
                .email("admin1@example.com")
                .password("password")
                .role(RoleType.ADMIN)
                .build();

        User adminUser2 = User.builder()
                .username("admin2")
                .email("admin2@example.com")
                .password("password")
                .role(RoleType.ADMIN)
                .build();

        User moderatorUser = User.builder()
                .username("moderator")
                .email("moderator@example.com")
                .password("password")
                .role(RoleType.MODERATOR)
                .build();

        userRepository.saveAll(List.of(adminUser1, adminUser2, moderatorUser));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> result = userRepository.findByRole(RoleType.ADMIN, pageable);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).allMatch(user -> user.getRole() == RoleType.ADMIN);
    }

    @Test
    @DisplayName("should handle user with null optional fields")
    void shouldHandleUserWithNullOptionalFields() {
        // Arrange
        User user = User.builder()
                .username("minimal_user")
                .email("minimal@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        // Act
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("minimal_user");
        assertThat(foundUser.getEmail()).isEqualTo("minimal@example.com");
        assertThat(foundUser.getFirstName()).isNull();
        assertThat(foundUser.getLastName()).isNull();
        assertThat(foundUser.getBio()).isNull();
    }
}
