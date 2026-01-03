package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.configs.SecurityConfig;
import com.zenith.dtos.requests.UpdateUserRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.entities.User;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.security.JwtService;
import com.zenith.security.SecurityUser;
import com.zenith.services.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private PageResponse<UserResponse> pageResponse;
    private UpdateUserRequest updateUserRequest;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UUID adminUserId = UUID.randomUUID();

        userResponse = new UserResponse(
                userId,
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                "Software Developer",
                RoleType.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                5,
                10);

        UserResponse adminUserResponse = new UserResponse(
                adminUserId,
                "adminuser",
                "admin@example.com",
                "Admin",
                "User",
                "System Administrator",
                RoleType.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now(),
                15,
                25);

        pageResponse = new PageResponse<>(0, 2, 2, 1, List.of(userResponse, adminUserResponse));

        updateUserRequest = new UpdateUserRequest(
                "updateduser", "updated@example.com", "newpassword123", "John", "Updated", "Senior Software Developer");

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.ADMIN)
                .build();
        user.setId(userId);
    }

    @Test
    @DisplayName("should get all users successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersSuccessfully() throws Exception {
        when(userService.getAllUsers(any(), any(PageRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.content[1].username").value("adminuser"));
    }

    @Test
    @DisplayName("should get all users filtered by role")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersFilteredByRole() throws Exception {
        when(userService.getAllUsers(eq(RoleType.USER), any(PageRequest.class)))
                .thenReturn(new PageResponse<>(0, 20, 1, 1, List.of(userResponse)));

        mockMvc.perform(get("/api/v1/users")
                        .param("role", "USER")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @DisplayName("should return 400 for invalid pagination parameters")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400ForInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("page", "-1").param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400ForInvalidSortParameters() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(userService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/users").param("sortBy", "invalidField")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should get current user successfully")
    void shouldGetCurrentUserSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(userService.getCurrentUser(anyString())).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/me").with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("should return 404 when current user not found")
    void shouldReturn404WhenCurrentUserNotFound() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(userService.getCurrentUser(anyString())).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/me").with(authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should get user by ID successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByIdSuccessfully() throws Exception {
        when(userService.getUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("should return 404 when user not found by ID")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserById(nonExistentUserId)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{userId}", nonExistentUserId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        UserResponse updatedUserResponse = new UserResponse(
                userId,
                "updateduser",
                "updated@example.com",
                "John",
                "Updated",
                "Senior Software Developer",
                RoleType.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                5,
                10);

        when(userService.updateUser(eq("testuser"), eq(userId), any(UpdateUserRequest.class)))
                .thenReturn(updatedUserResponse);

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @DisplayName("should return 403 when user tries to update another user")
    void shouldReturn403WhenUserTriesToUpdateAnotherUser() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        UUID otherUserId = UUID.randomUUID();
        when(userService.updateUser(eq("testuser"), eq(otherUserId), any(UpdateUserRequest.class)))
                .thenThrow(new ForbiddenException("Cannot update another user's profile"));

        mockMvc.perform(put("/api/v1/users/{userId}", otherUserId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 404 when updating non-existent user")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);
        when(userService.updateUser(eq("testuser"), eq(userId), any(UpdateUserRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 for invalid update request")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400ForInvalidUpdateRequest() throws Exception {
        UpdateUserRequest invalidRequest = new UpdateUserRequest(
                "", // invalid username
                "invalid-email",
                "short", // invalid password
                "", // invalid first name
                "", // invalid last name
                "");

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update user role successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserRoleSuccessfully() throws Exception {
        UserResponse updatedUserResponse = new UserResponse(
                userId,
                "testuser",
                "test@example.com",
                "John",
                "Doe",
                "Software Developer",
                RoleType.MODERATOR,
                LocalDateTime.now(),
                LocalDateTime.now(),
                5,
                10);

        when(userService.updateUserRole(userId, RoleType.MODERATOR)).thenReturn(updatedUserResponse);

        mockMvc.perform(patch("/api/v1/users/{userId}/role", userId).param("role", "MODERATOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MODERATOR"));
    }

    @Test
    @DisplayName("should return 404 when updating role for non-existent user")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUpdatingRoleForNonExistentUser() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.updateUserRole(nonExistentUserId, RoleType.MODERATOR))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(patch("/api/v1/users/{userId}/role", nonExistentUserId).param("role", "MODERATOR"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete user successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}", userId)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent user")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService)
                .deleteUser(nonExistentUserId);

        mockMvc.perform(delete("/api/v1/users/{userId}", nonExistentUserId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 for invalid role parameter")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400ForInvalidRoleParameter() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{userId}/role", userId).param("role", "INVALID_ROLE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 409 when updating to existing username")
    void shouldReturn409WhenUpdatingToExistingUsername() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);
        when(userService.updateUser(eq("testuser"), eq(userId), any(UpdateUserRequest.class)))
                .thenThrow(new DuplicateResourceException("Username already exists"));

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should return 409 when updating to existing email")
    void shouldReturn409WhenUpdatingToExistingEmail() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        UpdateUserRequest duplicateEmailRequest =
                new UpdateUserRequest("uniqueuser", "existing@example.com", "password123", "John", "Doe", "Developer");

        when(userService.updateUser(eq("testuser"), eq(userId), any(UpdateUserRequest.class)))
                .thenThrow(new DuplicateResourceException("Email already exists"));

        mockMvc.perform(put("/api/v1/users/{userId}", userId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isConflict());
    }
}
