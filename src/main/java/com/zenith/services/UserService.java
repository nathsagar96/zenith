package com.zenith.services;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<UserResponse> getAllUser(Pageable pageable) {
        log.info("Fetching all users");
        var users = userRepository.findAll(pageable);
        return new PageResponse<>(
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.stream().map(userMapper::toResponse).toList());
    }

    public PageResponse<UserResponse> getAllUserByRole(String role, Pageable pageable) {
        log.info("Fetching users with role: {}", role);
        RoleType roleType = RoleType.valueOf(role);
        var users = userRepository.findByRole(roleType, pageable);
        return new PageResponse<>(
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.stream().map(userMapper::toResponse).toList());
    }

    public UserResponse getUserById(long id) {
        log.info("Fetching user with id: {}", id);
        User user = findById(id);
        return userMapper.toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email:" + email));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with username: {}", request.username());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("User creation failed: Username '{}' already exists", request.username());
            throw new DuplicateResourceException("User with username: '" + request.username() + "' already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("User creation failed: Email '{}' already exists", request.email());
            throw new DuplicateResourceException("User with email: '" + request.username() + "' already exists");
        }

        User newUser = userMapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));

        User createdUser = userRepository.save(newUser);
        log.info("User created successfully with id: {}", createdUser.getId());
        return userMapper.toResponse(createdUser);
    }

    @Transactional
    public UserResponse updateUser(long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        User currentUser = getCurrentUser();
        if (currentUser.getId() != id && !currentUser.getRole().equals(RoleType.ADMIN)) {
            log.warn("User update failed: User {} attempted to update user {}", currentUser.getId(), id);
            throw new ForbiddenException("You can only update your own profile");
        }

        User existingUser = findById(id);

        if (request.username() != null && userRepository.existsByUsername(request.username())) {
            log.warn("User update failed: Username '{}' already exists", request.username());
            throw new DuplicateResourceException("User with username: '" + request.username() + "' already exists");
        }

        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            log.warn("User update failed: Email '{}' already exists", request.email());
            throw new DuplicateResourceException("User with email: '" + request.email() + "' already exists");
        }

        if (request.password() != null) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.firstName() != null && !request.firstName().isBlank()) {
            existingUser.setFirstName(request.firstName());
        }

        if (request.lastName() != null && !request.lastName().isBlank()) {
            existingUser.setLastName(request.lastName());
        }

        if (request.bio() != null && !request.bio().isBlank()) {
            existingUser.setBio(request.bio());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = findById(id);
        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);
    }

    @Transactional
    public void updateUserRole(Long id, RoleType role) {
        log.info("Updating role for user with id: {} to {}", id, role);

        User user = findById(id);
        user.setRole(role);
        userRepository.save(user);
        log.info("User role updated successfully with id: {}", id);
    }

    private User findById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));
    }
}
