package com.zenith.services;

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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public static List<String> ALLOWED_SORT_FIELDS =
            List.of("username", "email", "firstname", "lastname", "createdat", "updatedat");

    public void validateSortParams(String sortBy, String sortDirection) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new ValidationException("Invalid sort field: " + sortBy);
        }
        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            throw new ValidationException("Invalid sort direction: " + sortDirection);
        }
    }

    public PageResponse<UserResponse> getAllUsers(RoleType role, Pageable pageable) {
        Page<User> users;

        if (role != null) {
            users = userRepository.findByRole(role, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return new PageResponse<>(
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.stream().map(userMapper::toResponse).toList());
    }

    public UserResponse getCurrentUser(String username) {
        return userRepository
                .findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));
    }

    public UserResponse getUserById(UUID userId) {
        return userMapper.toResponse(findById(userId));
    }

    @Transactional
    public UserResponse updateUser(String username, UUID userId, UpdateUserRequest request) {
        User currentUser = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));

        if (!currentUser.getId().equals(userId) && !RoleType.ADMIN.equals(currentUser.getRole())) {
            throw new ForbiddenException("You can not update this profile");
        }

        User existingUser = findById(userId);

        if (request.username() != null && userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("User with username already exists");
        }

        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User with email already exists");
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

        return userMapper.toResponse(userRepository.save(existingUser));
    }

    @Transactional
    public UserResponse updateUserRole(UUID userId, RoleType role) {
        User existingUser = findById(userId);
        existingUser.setRole(role);
        return userMapper.toResponse(userRepository.save(existingUser));
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
