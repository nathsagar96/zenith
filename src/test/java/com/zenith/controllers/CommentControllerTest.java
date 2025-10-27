package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.enums.CommentStatus;
import com.zenith.security.JwtService;
import com.zenith.services.CommentService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtService jwtService;

    private CommentResponse commentResponse;
    private CreateCommentRequest createCommentRequest;
    private UpdateCommentRequest updateCommentRequest;
    private PageResponse<CommentResponse> pageResponse;

    @BeforeEach
    void setUp() {
        commentResponse = new CommentResponse(
                1L, "This is a test comment", "APPROVED", 1L, 1L, LocalDateTime.now(), LocalDateTime.now());

        createCommentRequest = new CreateCommentRequest("This is a test comment", 1L);
        updateCommentRequest = new UpdateCommentRequest("This is an updated comment");

        pageResponse = new PageResponse<>(0, 10, 1, 1, List.of(commentResponse));
    }

    @Test
    @DisplayName("should get all comments successfully")
    void shouldGetAllCommentsSuccessfully() throws Exception {
        when(commentService.getAllComments(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get approved comments by post successfully")
    void shouldGetApprovedCommentsByPostSuccessfully() throws Exception {
        when(commentService.getAllApprovedCommentsByPost(anyLong(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments/post/1").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get comments by author and status successfully")
    void shouldGetCommentsByAuthorAndStatusSuccessfully() throws Exception {
        when(commentService.getAllCommentsByAuthorAndStatus(anyLong(), anyString(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments/author/1/status/APPROVED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get pending comments successfully")
    void shouldGetPendingCommentsSuccessfully() throws Exception {
        when(commentService.getAllCommentsByStatus(any(CommentStatus.class), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments/pending").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get approved comments successfully")
    void shouldGetApprovedCommentsSuccessfully() throws Exception {
        when(commentService.getAllCommentsByStatus(any(CommentStatus.class), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments/approved").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get spam comments successfully")
    void shouldGetSpamCommentsSuccessfully() throws Exception {
        when(commentService.getAllCommentsByStatus(any(CommentStatus.class), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments/spam").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get archived comments successfully")
    void shouldGetArchivedCommentsSuccessfully() throws Exception {
        when(commentService.getAllCommentsByStatus(any(CommentStatus.class), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/comments/archived").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should get comment by id successfully")
    void shouldGetCommentByIdSuccessfully() throws Exception {
        when(commentService.getCommentById(anyLong())).thenReturn(commentResponse);

        mockMvc.perform(get("/api/v1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should create comment successfully")
    void shouldCreateCommentSuccessfully() throws Exception {
        when(commentService.createComment(any(CreateCommentRequest.class))).thenReturn(commentResponse);

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should update comment successfully")
    void shouldUpdateCommentSuccessfully() throws Exception {
        when(commentService.updateComment(anyLong(), any(UpdateCommentRequest.class)))
                .thenReturn(commentResponse);

        mockMvc.perform(put("/api/v1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is a test comment"));
    }

    @Test
    @DisplayName("should approve comment successfully")
    void shouldApproveCommentSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/v1/comments/1/approve")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should mark comment as spam successfully")
    void shouldMarkCommentAsSpamSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/v1/comments/1/spam")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should archive comment successfully")
    void shouldArchiveCommentSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/comments/1")).andExpect(status().isNoContent());
    }
}
