package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.configs.SecurityConfig;
import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.User;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.security.JwtService;
import com.zenith.security.SecurityUser;
import com.zenith.services.CommentService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PageResponse<CommentResponse> pageResponse;
    private CreateCommentRequest createCommentRequest;
    private UpdateCommentRequest updateCommentRequest;
    private UUID commentId;
    private UUID postId;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        postId = UUID.randomUUID();
        userId = UUID.randomUUID();

        CommentResponse commentResponse = new CommentResponse(
                commentId,
                "This is a test comment",
                CommentStatus.APPROVED,
                userId,
                postId,
                LocalDateTime.now(),
                LocalDateTime.now());

        CommentResponse anotherCommentResponse = new CommentResponse(
                UUID.randomUUID(),
                "Another test comment",
                CommentStatus.PENDING,
                userId,
                postId,
                LocalDateTime.now(),
                LocalDateTime.now());

        pageResponse = new PageResponse<>(0, 2, 2, 1, List.of(commentResponse, anotherCommentResponse));

        createCommentRequest = new CreateCommentRequest("New comment content");
        updateCommentRequest = new UpdateCommentRequest("Updated comment content");

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        user.setId(userId);
    }

    @Test
    @DisplayName("should get comments for post successfully")
    void shouldGetCommentsForPostSuccessfully() throws Exception {
        when(commentService.getAllComments(eq(postId), any(PageRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"))
                .andExpect(jsonPath("$.content[1].content").value("Another test comment"));
    }

    @Test
    @DisplayName("should return 400 for invalid pagination parameters")
    void shouldReturn400ForInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId)
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters")
    void shouldReturn400ForInvalidSortParameters() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(commentService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId).param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should create comment successfully")
    void shouldCreateCommentSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        CommentResponse createdCommentResponse = new CommentResponse(
                commentId,
                "New comment content",
                CommentStatus.PENDING,
                userId,
                postId,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(commentService.createComment(eq("testuser"), eq(postId), any(CreateCommentRequest.class)))
                .thenReturn(createdCommentResponse);

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("New comment content"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("should return 400 for invalid create comment request")
    void shouldReturn400ForInvalidCreateCommentRequest() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        CreateCommentRequest invalidRequest = new CreateCommentRequest(""); // invalid content

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 401 when user not authenticated for create comment")
    void shouldReturn401WhenUserNotAuthenticatedForCreateComment() throws Exception {
        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should update comment successfully")
    void shouldUpdateCommentSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        CommentResponse updatedCommentResponse = new CommentResponse(
                commentId,
                "Updated comment content",
                CommentStatus.APPROVED,
                userId,
                postId,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(commentService.updateComment(eq("testuser"), eq(commentId), any(UpdateCommentRequest.class)))
                .thenReturn(updatedCommentResponse);

        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment content"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("should return 403 when user tries to update another user's comment")
    void shouldReturn403WhenUserTriesToUpdateAnotherUsersComment() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(commentService.updateComment(eq("testuser"), eq(commentId), any(UpdateCommentRequest.class)))
                .thenThrow(new ForbiddenException("You are not allowed to edit this comment"));

        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 404 when updating non-existent comment")
    void shouldReturn404WhenUpdatingNonExistentComment() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(commentService.updateComment(eq("testuser"), eq(commentId), any(UpdateCommentRequest.class)))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete comment successfully")
    void shouldDeleteCommentSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(authentication(authentication)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 403 when user tries to delete another user's comment")
    void shouldReturn403WhenUserTriesToDeleteAnotherUsersComment() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        doThrow(new ForbiddenException("You are not allowed to delete this comment"))
                .when(commentService)
                .deleteComment(eq("testuser"), eq(commentId));

        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent comment")
    void shouldReturn404WhenDeletingNonExistentComment() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        doThrow(new ResourceNotFoundException("Comment not found"))
                .when(commentService)
                .deleteComment(eq("testuser"), eq(commentId));

        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                        .with(authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 401 when user not authenticated for delete comment")
    void shouldReturn401WhenUserNotAuthenticatedForDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isUnauthorized());
    }
}
