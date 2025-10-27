package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.enums.PostStatus;
import com.zenith.security.JwtService;
import com.zenith.services.PostService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostResponse postResponse;
    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;
    private PageResponse<PostResponse> pageResponse;

    @BeforeEach
    void setUp() {
        postResponse = new PostResponse(
                1L,
                "Test Post",
                "test-post",
                "This is test content",
                "PUBLISHED",
                "johndoe",
                "Technology",
                LocalDateTime.now(),
                LocalDateTime.now(),
                3,
                5);

        createPostRequest =
                new CreatePostRequest("Test Post", "This is test content", "Technology", Set.of("Java", "Spring"));

        updatePostRequest = new UpdatePostRequest(
                "Updated Post", "This is updated content", "Technology", Set.of("Java", "Spring"));

        pageResponse = new PageResponse<>(0, 10, 1, 1, List.of(postResponse));
    }

    @Test
    @DisplayName("should get all posts successfully")
    void shouldGetAllPostsSuccessfully() throws Exception {
        when(postService.getAllPosts(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get all public posts successfully")
    void shouldGetAllPublicPostsSuccessfully() throws Exception {
        when(postService.getAllPublishedPosts(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/public")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get public posts by author successfully")
    void shouldGetPublicPostsByAuthorSuccessfully() throws Exception {
        when(postService.getAllPublishedPostsByAuthor(anyLong(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/author/1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get posts by author and status successfully")
    void shouldGetPostsByAuthorAndStatusSuccessfully() throws Exception {
        when(postService.getAllPostsByAuthorAndStatus(anyLong(), anyString(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/author/1/status/PUBLISHED")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get draft posts successfully")
    void shouldGetDraftPostsSuccessfully() throws Exception {
        when(postService.getAllPostsByStatus(any(PostStatus.class), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/drafts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get published posts successfully")
    void shouldGetPublishedPostsSuccessfully() throws Exception {
        when(postService.getAllPostsByStatus(any(PostStatus.class), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/published")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get archived posts successfully")
    void shouldGetArchivedPostsSuccessfully() throws Exception {
        when(postService.getAllPostsByStatus(any(PostStatus.class), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/archived")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should get post by id successfully")
    void shouldGetPostByIdSuccessfully() throws Exception {
        when(postService.getPostById(anyLong())).thenReturn(postResponse);

        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    @DisplayName("should create post successfully")
    void shouldCreatePostSuccessfully() throws Exception {
        when(postService.createPost(any(CreatePostRequest.class))).thenReturn(postResponse);

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    @DisplayName("should update post successfully")
    void shouldUpdatePostSuccessfully() throws Exception {
        when(postService.updatePost(anyLong(), any(UpdatePostRequest.class))).thenReturn(postResponse);

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    @DisplayName("should publish post successfully")
    void shouldPublishPostSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/v1/posts/1/publish")).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should archive post successfully")
    void shouldArchivePostSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")).andExpect(status().isNoContent());
    }
}
