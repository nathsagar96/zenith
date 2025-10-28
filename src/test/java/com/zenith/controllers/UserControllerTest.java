package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.CreateUserRequest;
import com.zenith.dtos.requests.UpdateUserRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.UserResponse;
import com.zenith.enums.RoleType;
import com.zenith.security.JwtService;
import com.zenith.services.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private PageResponse<UserResponse> pageResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                "Test",
                "User",
                "This is a test bio",
                "USER",
                LocalDateTime.now(),
                LocalDateTime.now(),
                5,
                10);

        createUserRequest = new CreateUserRequest(
                "testuser", "test@example.com", "password", "Test", "User", "This is a test bio", "USER");

        updateUserRequest = new UpdateUserRequest(
                "updateduser", "updated@example.com", "newpassword", "Updated", "User", "This is an updated bio");

        pageResponse = new PageResponse<>(0, 10, 1, 1, List.of(userResponse));
    }

    @Test
    @DisplayName("should get all users successfully")
    void shouldGetAllUsersSuccessfully() throws Exception {
        PageRequest request = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        when(userService.getAllUsers(null, request)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @DisplayName("should get users by role successfully")
    void shouldGetUsersByRoleSuccessfully() throws Exception {
        when(userService.getAllUsers(any(RoleType.class), any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @DisplayName("should get user by id successfully")
    void shouldGetUserByIdSuccessfully() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1/role").param("role", "ADMIN")).andExpect(status().isNoContent());

        mockMvc.perform(patch("/api/v1/users/1/role").param("role", "USER")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should delete user successfully")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")).andExpect(status().isNoContent());
    }
}
