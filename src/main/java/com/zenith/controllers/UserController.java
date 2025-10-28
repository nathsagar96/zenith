package com.zenith.controllers;

import com.zenith.dtos.requests.CreateUserRequest;
import com.zenith.dtos.requests.UpdateUserRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.enums.RoleType;
import com.zenith.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public PageResponse<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get all users");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<UserResponse> response = userService.getAllUser(pageable);
        log.info("Returning {} users", response.getTotalElements());
        return response;
    }

    @GetMapping("/role/{role}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Retrieves a paginated list of users by role")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public PageResponse<UserResponse> getAllUsersByRole(
            @PathVariable("role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get users with role: {}", role);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<UserResponse> response = userService.getAllUserByRole(role, pageable);
        log.info("Returning {} users with role: {}", response.getTotalElements(), role);
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserResponse getUserById(@PathVariable("id") Long id) {
        log.info("Received request to get user with id: {}", id);
        UserResponse response = userService.getUserById(id);
        log.info("Returning user with id: {}", id);
        return response;
    }

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserResponse getUserByEmail(@PathVariable("email") String email) {
        log.info("Received request to get user with email: {}", email);
        UserResponse response = userService.getUserByEmail(email);
        log.info("Returning user with email: {}", email);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user details")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Received request to create user with username: {}", request.username());
        UserResponse response = userService.createUser(request);
        log.info("User created successfully with id: {}", response.id());
        return response;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a user", description = "Updates an existing user with the provided details")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user details")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserResponse updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Received request to update user with id: {}", id);
        UserResponse response = userService.updateUser(id, request);
        log.info("User updated successfully with id: {}", id);
        return response;
    }

    @PatchMapping("/{id}/role/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Make user an admin", description = "Promotes a user to admin role")
    @ApiResponse(responseCode = "204", description = "User promoted to admin successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void makeAdmin(@PathVariable("id") Long id) {
        log.info("Received request to make user with id: {} an admin", id);
        userService.updateUserRole(id, RoleType.ADMIN);
        log.info("User with id: {} promoted to admin successfully", id);
    }

    @PatchMapping("/{id}/role/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Make user a regular user", description = "Demotes a user to regular user role")
    @ApiResponse(responseCode = "204", description = "User demoted to regular user successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void makeUser(@PathVariable("id") Long id) {
        log.info("Received request to make user with id: {} a regular user", id);
        userService.updateUserRole(id, RoleType.USER);
        log.info("User with id: {} demoted to regular user successfully", id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void deleteUser(@PathVariable("id") Long id) {
        log.info("Received request to delete user with id: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully with id: {}", id);
    }
}
