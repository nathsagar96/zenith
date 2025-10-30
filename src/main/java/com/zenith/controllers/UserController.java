package com.zenith.controllers;

import com.zenith.dtos.requests.UpdateUserRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.enums.RoleType;
import com.zenith.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Retrieve a paginated list of all users with optional role filtering",
            parameters = {
                @Parameter(
                        name = "page",
                        description = "Page number (0-based index)",
                        schema = @Schema(defaultValue = "0", minimum = "0")),
                @Parameter(
                        name = "size",
                        description = "Page size",
                        schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100")),
                @Parameter(
                        name = "sortBy",
                        description =
                                "Field to sort by (e.g., username, email, firstName, lastName, createdAt, updatedAt)"),
                @Parameter(
                        name = "sortDirection",
                        description = "Sort direction (ASC or DESC)",
                        schema = @Schema(allowableValues = {"ASC", "DESC"})),
                @Parameter(name = "role", description = "Optional role to filter by"),
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PageResponse.class)))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<UserResponse> getAllUsers(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) RoleType role) {
        userService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return userService.getAllUsers(role, pageable);
    }

    @Operation(
            summary = "Get current user",
            description = "Retrieve the currently authenticated user's information",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class)))
            })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication.getName());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class)))
            })
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable("userId")
                    UUID userId) {
        return userService.getUserById(userId);
    }

    @Operation(
            summary = "Update a user",
            description = "Update an existing user's information",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class)))
            })
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable("userId") UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    @Operation(
            summary = "Update user role",
            description = "Update a user's role",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User role updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class)))
            })
    @PatchMapping("/{userId}/role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUserRole(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable("userId") UUID userId,
            @Parameter(description = "Role to set", required = true) @RequestParam("role") RoleType role) {
        return userService.updateUserRole(userId, role);
    }

    @Operation(
            summary = "Delete a user",
            description = "Delete a user by their ID",
            responses = {@ApiResponse(responseCode = "204", description = "User deleted successfully")})
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable("userId") UUID userId) {
        userService.deleteUser(userId);
    }
}
