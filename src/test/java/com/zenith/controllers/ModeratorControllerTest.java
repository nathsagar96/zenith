package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.PostStatus;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.security.JwtService;
import com.zenith.services.CommentService;
import com.zenith.services.PostService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ModeratorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ModeratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private CommentService commentService;

    private PostResponse postResponse;
    private CommentResponse commentResponse;
    private PageResponse<PostResponse> postPageResponse;
    private PageResponse<CommentResponse> commentPageResponse;
    private UUID postId;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID otherPostId = UUID.randomUUID();

        commentId = UUID.randomUUID();
        UUID commentAuthorId = UUID.randomUUID();
        UUID commentPostId = UUID.randomUUID();
        UUID otherCommentId = UUID.randomUUID();

        postResponse = new PostResponse(
                postId,
                "Getting Started with Spring Boot",
                "This is the content of the post",
                PostStatus.PUBLISHED,
                authorId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                3,
                5);

        PostResponse otherPostResponse = new PostResponse(
                otherPostId,
                "Advanced Java Techniques",
                "This is another post content",
                PostStatus.DRAFT,
                authorId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                2,
                3);

        postPageResponse = new PageResponse<>(0, 20, 2, 1, List.of(postResponse, otherPostResponse));

        commentResponse = new CommentResponse(
                commentId,
                "This is a great post!",
                CommentStatus.APPROVED,
                commentPostId,
                commentAuthorId,
                LocalDateTime.now(),
                LocalDateTime.now());

        CommentResponse otherCommentResponse = new CommentResponse(
                otherCommentId,
                "I learned a lot from this",
                CommentStatus.PENDING,
                commentPostId,
                commentAuthorId,
                LocalDateTime.now(),
                LocalDateTime.now());

        commentPageResponse = new PageResponse<>(0, 20, 2, 1, List.of(commentResponse, otherCommentResponse));
    }

    @Test
    @DisplayName("should get posts by status successfully")
    void shouldGetPostsByStatusSuccessfully() throws Exception {
        when(postService.getPostsByStatus(any(PostStatus.class), any(PageRequest.class)))
                .thenReturn(postPageResponse);

        mockMvc.perform(get("/api/v1/moderator/posts")
                        .param("status", "PUBLISHED")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Getting Started with Spring Boot"))
                .andExpect(jsonPath("$.content[1].title").value("Advanced Java Techniques"));
    }

    @Test
    @DisplayName("should return 400 for invalid post status parameter")
    void shouldReturn400ForInvalidPostStatusParameter() throws Exception {
        mockMvc.perform(get("/api/v1/moderator/posts").param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters in posts")
    void shouldReturn400ForInvalidSortParametersInPosts() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(postService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/moderator/posts")
                        .param("status", "PUBLISHED")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update post status successfully")
    void shouldUpdatePostStatusSuccessfully() throws Exception {
        PostResponse updatedPostResponse = new PostResponse(
                postId,
                "Getting Started with Spring Boot",
                "This is the content of the post",
                PostStatus.ARCHIVED,
                postResponse.authorId(),
                postResponse.categoryId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                3,
                5);

        when(postService.updatePostStatus(postId, PostStatus.ARCHIVED)).thenReturn(updatedPostResponse);

        mockMvc.perform(patch("/api/v1/moderator/posts/{postId}/status", postId).param("status", "ARCHIVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    @Test
    @DisplayName("should return 404 when updating status for non-existent post")
    void shouldReturn404WhenUpdatingStatusForNonExistentPost() throws Exception {
        UUID nonExistentPostId = UUID.randomUUID();
        when(postService.updatePostStatus(nonExistentPostId, PostStatus.ARCHIVED))
                .thenThrow(new ResourceNotFoundException("Post not found"));

        mockMvc.perform(patch("/api/v1/moderator/posts/{postId}/status", nonExistentPostId)
                        .param("status", "ARCHIVED"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 for invalid post status update")
    void shouldReturn400ForInvalidPostStatusUpdate() throws Exception {
        mockMvc.perform(patch("/api/v1/moderator/posts/{postId}/status", postId).param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should get comments by status successfully")
    void shouldGetCommentsByStatusSuccessfully() throws Exception {
        when(commentService.getCommentsByStatus(any(CommentStatus.class), any(PageRequest.class)))
                .thenReturn(commentPageResponse);

        mockMvc.perform(get("/api/v1/moderator/comments")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].content").value("This is a great post!"))
                .andExpect(jsonPath("$.content[1].content").value("I learned a lot from this"));
    }

    @Test
    @DisplayName("should return 400 for invalid comment status parameter")
    void shouldReturn400ForInvalidCommentStatusParameter() throws Exception {
        mockMvc.perform(get("/api/v1/moderator/comments").param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters in comments")
    void shouldReturn400ForInvalidSortParametersInComments() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(commentService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/moderator/comments")
                        .param("status", "PENDING")
                        .param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update comment status successfully")
    void shouldUpdateCommentStatusSuccessfully() throws Exception {
        CommentResponse updatedCommentResponse = new CommentResponse(
                commentId,
                "This is a great post!",
                CommentStatus.APPROVED,
                commentResponse.postId(),
                commentResponse.authorId(),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(commentService.updateCommentStatus(commentId, CommentStatus.APPROVED))
                .thenReturn(updatedCommentResponse);

        mockMvc.perform(patch("/api/v1/moderator/comments/{commentId}/status", commentId)
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("should return 404 when updating status for non-existent comment")
    void shouldReturn404WhenUpdatingStatusForNonExistentComment() throws Exception {
        UUID nonExistentCommentId = UUID.randomUUID();
        when(commentService.updateCommentStatus(nonExistentCommentId, CommentStatus.APPROVED))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(patch("/api/v1/moderator/comments/{commentId}/status", nonExistentCommentId)
                        .param("status", "APPROVED"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 for invalid comment status update")
    void shouldReturn400ForInvalidCommentStatusUpdate() throws Exception {
        mockMvc.perform(patch("/api/v1/moderator/comments/{commentId}/status", commentId)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid pagination parameters in posts")
    void shouldReturn400ForInvalidPaginationParametersInPosts() throws Exception {
        mockMvc.perform(get("/api/v1/moderator/posts")
                        .param("status", "PUBLISHED")
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid pagination parameters in comments")
    void shouldReturn400ForInvalidPaginationParametersInComments() throws Exception {
        mockMvc.perform(get("/api/v1/moderator/comments")
                        .param("status", "PENDING")
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }
}
