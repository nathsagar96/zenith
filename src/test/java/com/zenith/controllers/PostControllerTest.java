package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.configs.SecurityConfig;
import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.entities.User;
import com.zenith.enums.PostStatus;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.UnauthorizedException;
import com.zenith.exceptions.ValidationException;
import com.zenith.security.JwtService;
import com.zenith.security.SecurityUser;
import com.zenith.services.PostService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostResponse postResponse;
    private PostResponse publishedPostResponse;
    private PageResponse<PostResponse> pageResponse;
    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;
    private UUID postId;
    private UUID categoryId;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        userId = UUID.randomUUID();

        postResponse = new PostResponse(
                postId,
                "Test Post",
                "This is test content",
                PostStatus.DRAFT,
                userId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                2,
                0);

        publishedPostResponse = new PostResponse(
                postId,
                "Published Post",
                "This is published content",
                PostStatus.PUBLISHED,
                userId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                3,
                5);

        PostResponse anotherPostResponse = new PostResponse(
                UUID.randomUUID(),
                "Another Post",
                "Another content",
                PostStatus.PUBLISHED,
                userId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1,
                2);

        pageResponse = new PageResponse<>(0, 2, 2, 1, List.of(postResponse, anotherPostResponse));

        createPostRequest =
                new CreatePostRequest("New Post", "This is new content", categoryId, Set.of("spring", "java"));

        updatePostRequest = new UpdatePostRequest(
                "Updated Post", "This is updated content", categoryId, Set.of("spring", "java", "testing"));

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();
        user.setId(userId);
    }

    @Test
    @DisplayName("should get published posts successfully")
    void shouldGetPublishedPostsSuccessfully() throws Exception {
        when(postService.getPublishedPosts(any(), any(), any(PageRequest.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"))
                .andExpect(jsonPath("$.content[1].title").value("Another Post"));
    }

    @Test
    @DisplayName("should get published posts filtered by category")
    void shouldGetPublishedPostsFilteredByCategory() throws Exception {
        when(postService.getPublishedPosts(eq(categoryId), any(), any(PageRequest.class)))
                .thenReturn(new PageResponse<>(0, 20, 1, 1, List.of(publishedPostResponse)));

        mockMvc.perform(get("/api/v1/posts")
                        .param("categoryId", categoryId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Published Post"));
    }

    @Test
    @DisplayName("should get published posts filtered by tag")
    void shouldGetPublishedPostsFilteredByTag() throws Exception {
        when(postService.getPublishedPosts(any(), eq("spring"), any(PageRequest.class)))
                .thenReturn(new PageResponse<>(0, 20, 1, 1, List.of(publishedPostResponse)));

        mockMvc.perform(get("/api/v1/posts")
                        .param("tag", "spring")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Published Post"));
    }

    @Test
    @DisplayName("should return 400 for invalid pagination parameters")
    void shouldReturn400ForInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/api/v1/posts").param("page", "-1").param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters")
    void shouldReturn400ForInvalidSortParameters() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(postService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/posts").param("sortBy", "invalidField")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should get post by ID successfully when published")
    void shouldGetPostByIdSuccessfullyWhenPublished() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(false);

        when(postService.getPostById(anyString(), eq(postId))).thenReturn(publishedPostResponse);

        mockMvc.perform(get("/api/v1/posts/{postId}", postId).with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId.toString()))
                .andExpect(jsonPath("$.title").value("Published Post"));
    }

    @Test
    @DisplayName("should get post by ID successfully when user is owner")
    void shouldGetPostByIdSuccessfullyWhenUserIsOwner() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.getPostById(eq("testuser"), eq(postId))).thenReturn(postResponse);

        mockMvc.perform(get("/api/v1/posts/{postId}", postId).with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId.toString()))
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    @DisplayName("should return 404 when post not found by ID")
    void shouldReturn404WhenPostNotFoundById() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(false);

        UUID nonExistentPostId = UUID.randomUUID();
        when(postService.getPostById(anyString(), eq(nonExistentPostId)))
                .thenThrow(new ResourceNotFoundException("Post not found"));

        mockMvc.perform(get("/api/v1/posts/{postId}", nonExistentPostId).with(authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 403 when user tries to access non-published post they don't own")
    void shouldReturn403WhenUserTriesToAccessNonPublishedPostTheyDontOwn() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.getPostById(eq("testuser"), eq(postId)))
                .thenThrow(new ForbiddenException("You are not allowed to view this post"));

        mockMvc.perform(get("/api/v1/posts/{postId}", postId).with(authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should get current user's posts successfully")
    void shouldGetCurrentUserPostsSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.getMyPosts(eq("testuser"), any(), any(PageRequest.class)))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(authentication(authentication))
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"))
                .andExpect(jsonPath("$.content[1].title").value("Another Post"));
    }

    @Test
    @DisplayName("should get current user's posts filtered by status")
    void shouldGetCurrentUserPostsFilteredByStatus() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.getMyPosts(eq("testuser"), eq(PostStatus.DRAFT), any(PageRequest.class)))
                .thenReturn(new PageResponse<>(0, 20, 1, 1, List.of(postResponse)));

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(authentication(authentication))
                        .param("status", "DRAFT")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Post"));
    }

    @Test
    @DisplayName("should return 401 when user not authenticated for my posts")
    void shouldReturn401WhenUserNotAuthenticatedForMyPosts() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.getMyPosts(any(), any(), any(PageRequest.class)))
                .thenThrow(new UnauthorizedException("You are not allowed to view this post"));

        mockMvc.perform(get("/api/v1/posts/my").with(authentication(authentication)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return 400 for invalid status parameter in my posts")
    void shouldReturn400ForInvalidStatusParameterInMyPosts() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(authentication(authentication))
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should create post successfully")
    void shouldCreatePostSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        PostResponse createdPostResponse = new PostResponse(
                postId,
                "New Post",
                "This is new content",
                PostStatus.DRAFT,
                userId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                2,
                0);

        when(postService.createPost(eq("testuser"), any(CreatePostRequest.class)))
                .thenReturn(createdPostResponse);

        mockMvc.perform(post("/api/v1/posts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.content").value("This is new content"));
    }

    @Test
    @DisplayName("should return 400 for invalid create post request")
    void shouldReturn400ForInvalidCreatePostRequest() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        CreatePostRequest invalidRequest = new CreatePostRequest(
                "", // invalid title
                "", // invalid content
                null, // invalid category
                Set.of());

        mockMvc.perform(post("/api/v1/posts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 401 when user not authenticated for create post")
    void shouldReturn401WhenUserNotAuthenticatedForCreatePost() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPostRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should update post successfully")
    void shouldUpdatePostSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        PostResponse updatedPostResponse = new PostResponse(
                postId,
                "Updated Post",
                "This is updated content",
                PostStatus.DRAFT,
                userId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                3,
                0);

        when(postService.updatePost(eq("testuser"), eq(postId), any(UpdatePostRequest.class)))
                .thenReturn(updatedPostResponse);

        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post"))
                .andExpect(jsonPath("$.content").value("This is updated content"));
    }

    @Test
    @DisplayName("should return 403 when user tries to update another user's post")
    void shouldReturn403WhenUserTriesToUpdateAnotherUsersPost() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.updatePost(eq("testuser"), eq(postId), any(UpdatePostRequest.class)))
                .thenThrow(new ForbiddenException("You are not allowed to edit / delete this post"));

        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 404 when updating non-existent post")
    void shouldReturn404WhenUpdatingNonExistentPost() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        when(postService.updatePost(eq("testuser"), eq(postId), any(UpdatePostRequest.class)))
                .thenThrow(new ResourceNotFoundException("Post not found"));

        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete post successfully")
    void shouldDeletePostSuccessfully() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        mockMvc.perform(delete("/api/v1/posts/{postId}", postId).with(authentication(authentication)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 403 when user tries to delete another user's post")
    void shouldReturn403WhenUserTriesToDeleteAnotherUsersPost() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        doThrow(new ForbiddenException("You are not allowed to edit / delete this post"))
                .when(postService)
                .deletePost(eq("testuser"), eq(postId));

        mockMvc.perform(delete("/api/v1/posts/{postId}", postId).with(authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent post")
    void shouldReturn404WhenDeletingNonExistentPost() throws Exception {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication =
                new TestingAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        authentication.setAuthenticated(true);

        doThrow(new ResourceNotFoundException("Post not found"))
                .when(postService)
                .deletePost(eq("testuser"), eq(postId));

        mockMvc.perform(delete("/api/v1/posts/{postId}", postId).with(authentication(authentication)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 401 when user not authenticated for delete post")
    void shouldReturn401WhenUserNotAuthenticatedForDeletePost() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/{postId}", postId)).andExpect(status().isUnauthorized());
    }
}
