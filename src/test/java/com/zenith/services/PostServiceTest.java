package com.zenith.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.entities.Category;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import com.zenith.entities.User;
import com.zenith.enums.PostStatus;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.UnauthorizedException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.PostMapper;
import com.zenith.repositories.CategoryRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.TagRepository;
import com.zenith.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;
    private Post post;
    private PostResponse postResponse;
    private User user;
    private User adminUser;
    private User moderatorUser;
    private User otherUser;
    private Category category;
    private Tag tag;
    private UUID postId;
    private UUID categoryId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup common test data
        createPostRequest = new CreatePostRequest(
                "Test Post Title", "This is test post content", UUID.randomUUID(), Set.of("spring", "java"));

        updatePostRequest = new UpdatePostRequest(
                "Updated Post Title",
                "This is updated post content",
                UUID.randomUUID(),
                Set.of("spring", "java", "testing"));

        postId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        adminUser = User.builder()
                .username("adminuser")
                .email("admin@example.com")
                .password("password")
                .role(RoleType.ADMIN)
                .build();

        moderatorUser = User.builder()
                .username("moderatoruser")
                .email("moderator@example.com")
                .password("password")
                .role(RoleType.MODERATOR)
                .build();

        otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        category = Category.builder().name("Technology").build();

        tag = Tag.builder().name("spring").build();

        post = Post.builder()
                .title("Test Post")
                .content("Test content")
                .status(PostStatus.PUBLISHED)
                .author(user)
                .category(category)
                .build();

        postResponse = new PostResponse(
                postId,
                "Test Post",
                "Test content",
                PostStatus.PUBLISHED,
                user.getId(),
                categoryId,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                0,
                0);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("shouldValidateSortParamsSuccessfullyWhenValid")
    void shouldValidateSortParamsSuccessfullyWhenValid() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            postService.validateSortParams("title", "asc");
            postService.validateSortParams("createdat", "desc");
        });
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenSortFieldIsInvalid")
    void shouldThrowValidationExceptionWhenSortFieldIsInvalid() {
        // Act & Assert
        ValidationException exception =
                assertThrows(ValidationException.class, () -> postService.validateSortParams("invalidField", "asc"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort field: invalidField");
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenSortDirectionIsInvalid")
    void shouldThrowValidationExceptionWhenSortDirectionIsInvalid() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> postService.validateSortParams("title", "invalidDirection"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort direction: invalidDirection");
    }

    @Test
    @DisplayName("shouldGetPublishedPostsSuccessfully")
    void shouldGetPublishedPostsSuccessfully() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findPublished(pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PageResponse<PostResponse> result = postService.getPublishedPosts(null, null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(postResponse);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(postRepository, times(1)).findPublished(pageable);
        verify(postMapper, times(1)).toResponse(post);
    }

    @Test
    @DisplayName("shouldGetPublishedPostsByCategorySuccessfully")
    void shouldGetPublishedPostsByCategorySuccessfully() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findByCategoryId(categoryId, pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PageResponse<PostResponse> result = postService.getPublishedPosts(categoryId, null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(postResponse);

        verify(postRepository, times(1)).findByCategoryId(categoryId, pageable);
        verify(postRepository, never()).findByTagsName(any(), any());
        verify(postRepository, never()).findPublished(any());
    }

    @Test
    @DisplayName("shouldGetPublishedPostsByTagSuccessfully")
    void shouldGetPublishedPostsByTagSuccessfully() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findByTagsName("spring", pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PageResponse<PostResponse> result = postService.getPublishedPosts(null, "spring", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(postResponse);

        verify(postRepository, times(1)).findByTagsName("spring", pageable);
        verify(postRepository, never()).findByCategoryId(any(), any());
        verify(postRepository, never()).findPublished(any());
    }

    @Test
    @DisplayName("shouldGetMyPostsSuccessfully")
    void shouldGetMyPostsSuccessfully() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findByAuthorId(user.getId(), pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PageResponse<PostResponse> result = postService.getMyPosts(user.getUsername(), null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(postResponse);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findByAuthorId(user.getId(), pageable);
    }

    @Test
    @DisplayName("shouldThrowUnauthorizedExceptionWhenUserNotFoundForGetMyPosts")
    void shouldThrowUnauthorizedExceptionWhenUserNotFoundForGetMyPosts() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> postService.getMyPosts(user.getUsername(), null, pageable));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, never()).findByAuthorId(any(), any());
    }

    @Test
    @DisplayName("shouldGetMyPostsByStatusSuccessfully")
    void shouldGetMyPostsByStatusSuccessfully() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findByAuthorIdAndStatus(user.getId(), PostStatus.DRAFT, pageable))
                .thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PageResponse<PostResponse> result = postService.getMyPosts(user.getUsername(), PostStatus.DRAFT, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(postResponse);

        verify(postRepository, times(1)).findByAuthorIdAndStatus(user.getId(), PostStatus.DRAFT, pageable);
        verify(postRepository, never()).findByAuthorId(any(), any());
    }

    @Test
    @DisplayName("shouldGetPostsByStatusSuccessfully")
    void shouldGetPostsByStatusSuccessfully() {
        // Arrange
        Page<Post> postPage = new PageImpl<>(List.of(post));
        when(postRepository.findByStatus(PostStatus.DRAFT, pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PageResponse<PostResponse> result = postService.getPostsByStatus(PostStatus.DRAFT, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(postResponse);

        verify(postRepository, times(1)).findByStatus(PostStatus.DRAFT, pageable);
    }

    @Test
    @DisplayName("shouldGetPostByIdSuccessfullyWhenPublished")
    void shouldGetPostByIdSuccessfullyWhenPublished() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.getPostById(null, postId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);

        verify(postRepository, times(1)).findById(postId);
        verify(postMapper, times(1)).toResponse(post);
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    @DisplayName("shouldGetPostByIdSuccessfullyWhenUserHasAccess")
    void shouldGetPostByIdSuccessfullyWhenUserHasAccess() {
        // Arrange
        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(user)
                .category(category)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(draftPost));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postMapper.toResponse(draftPost)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.getPostById(user.getUsername(), postId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postMapper, times(1)).toResponse(draftPost);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenPostNotFound")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFound() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(user.getUsername(), postId));

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    @DisplayName("shouldThrowForbiddenExceptionWhenUserNotAuthorizedToViewPost")
    void shouldThrowForbiddenExceptionWhenUserNotAuthorizedToViewPost() {
        // Arrange
        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(otherUser)
                .category(category)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(draftPost));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> postService.getPostById(user.getUsername(), postId));

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    @DisplayName("shouldCreatePostSuccessfully")
    void shouldCreatePostSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(createPostRequest.categoryId())).thenReturn(Optional.of(category));
        when(tagRepository.findByNameIgnoreCase("spring")).thenReturn(Optional.of(tag));
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(Tag.builder().name("java").build()));
        when(postMapper.toEntity(createPostRequest)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.createPost(user.getUsername(), createPostRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);
        assertThat(post.getAuthor()).isEqualTo(user);
        assertThat(post.getCategory()).isEqualTo(category);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(categoryRepository, times(1)).findById(createPostRequest.categoryId());
        verify(tagRepository, times(1)).findByNameIgnoreCase("spring");
        verify(tagRepository, times(1)).findByNameIgnoreCase("java");
        verify(postMapper, times(1)).toEntity(createPostRequest);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toResponse(post);
    }

    @Test
    @DisplayName("shouldThrowUnauthorizedExceptionWhenUserNotFoundForCreatePost")
    void shouldThrowUnauthorizedExceptionWhenUserNotFoundForCreatePost() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> postService.createPost(user.getUsername(), createPostRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(categoryRepository, never()).findById(any());
        verify(postMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundForCreatePost")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundForCreatePost() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(createPostRequest.categoryId())).thenReturn(Optional.empty());
        when(postMapper.toEntity(createPostRequest)).thenReturn(Post.builder().build());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class, () -> postService.createPost(user.getUsername(), createPostRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(categoryRepository, times(1)).findById(createPostRequest.categoryId());
        verify(postMapper, times(1)).toEntity(any());
    }

    @Test
    @DisplayName("shouldUpdatePostSuccessfully")
    void shouldUpdatePostSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(updatePostRequest.categoryId())).thenReturn(Optional.of(category));
        when(tagRepository.findByNameIgnoreCase("spring")).thenReturn(Optional.of(tag));
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(Tag.builder().name("java").build()));
        when(tagRepository.findByNameIgnoreCase("testing"))
                .thenReturn(Optional.of(Tag.builder().name("testing").build()));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.updatePost(user.getUsername(), postId, updatePostRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);
        assertThat(post.getTitle()).isEqualTo(updatePostRequest.title());
        assertThat(post.getContent()).isEqualTo(updatePostRequest.content());
        assertThat(post.getCategory()).isEqualTo(category);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(categoryRepository, times(1)).findById(updatePostRequest.categoryId());
        verify(tagRepository, times(1)).findByNameIgnoreCase("spring");
        verify(tagRepository, times(1)).findByNameIgnoreCase("java");
        verify(tagRepository, times(1)).findByNameIgnoreCase("testing");
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toResponse(post);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenUserNotFoundForUpdatePost")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundForUpdatePost() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.updatePost(user.getUsername(), postId, updatePostRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, never()).findById(any());
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenPostNotFoundForUpdate")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForUpdate() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> postService.updatePost(user.getUsername(), postId, updatePostRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldThrowForbiddenExceptionWhenUserNotOwnerForUpdatePost")
    void shouldThrowForbiddenExceptionWhenUserNotOwnerForUpdatePost() {
        // Arrange
        Post otherUsersPost = Post.builder()
                .title("Other user's post")
                .content("Other user's content")
                .status(PostStatus.PUBLISHED)
                .author(otherUser)
                .category(category)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(otherUsersPost));

        // Act & Assert
        assertThrows(
                ForbiddenException.class, () -> postService.updatePost(user.getUsername(), postId, updatePostRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldDeletePostSuccessfully")
    void shouldDeletePostSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        assertDoesNotThrow(() -> postService.deletePost(user.getUsername(), postId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenUserNotFoundForDeletePost")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundForDeletePost() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(user.getUsername(), postId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, never()).findById(any());
        verify(postRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenPostNotFoundForDelete")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForDelete() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(user.getUsername(), postId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldThrowForbiddenExceptionWhenUserNotOwnerForDeletePost")
    void shouldThrowForbiddenExceptionWhenUserNotOwnerForDeletePost() {
        // Arrange
        Post otherUsersPost = Post.builder()
                .title("Other user's post")
                .content("Other user's content")
                .status(PostStatus.PUBLISHED)
                .author(otherUser)
                .category(category)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(otherUsersPost));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> postService.deletePost(user.getUsername(), postId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldUpdatePostStatusSuccessfully")
    void shouldUpdatePostStatusSuccessfully() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.updatePostStatus(postId, PostStatus.PUBLISHED);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toResponse(post);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenPostNotFoundForStatusUpdate")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForStatusUpdate() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.updatePostStatus(postId, PostStatus.PUBLISHED));

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("shouldHandleEmptyPageWhenNoPublishedPostsFound")
    void shouldHandleEmptyPageWhenNoPublishedPostsFound() {
        // Arrange
        Page<Post> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(postRepository.findPublished(pageable)).thenReturn(emptyPage);

        // Act
        PageResponse<PostResponse> result = postService.getPublishedPosts(null, null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);

        verify(postRepository, times(1)).findPublished(pageable);
        verify(postMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("shouldHandleMultiplePostsInPageResponse")
    void shouldHandleMultiplePostsInPageResponse() {
        // Arrange
        Post post2 = Post.builder()
                .title("Second Post")
                .content("Second post content")
                .status(PostStatus.PUBLISHED)
                .author(user)
                .category(category)
                .build();

        PostResponse postResponse2 = new PostResponse(
                UUID.randomUUID(),
                "Second Post",
                "Second post content",
                PostStatus.PUBLISHED,
                user.getId(),
                categoryId,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now(),
                0,
                0);

        Page<Post> postPage = new PageImpl<>(List.of(post, post2));
        when(postRepository.findPublished(pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(postResponse);
        when(postMapper.toResponse(post2)).thenReturn(postResponse2);

        // Act
        PageResponse<PostResponse> result = postService.getPublishedPosts(null, null, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(postResponse, postResponse2);

        verify(postRepository, times(1)).findPublished(pageable);
        verify(postMapper, times(1)).toResponse(post);
        verify(postMapper, times(1)).toResponse(post2);
    }

    @Test
    @DisplayName("shouldAllowAdminToUpdateAnyPost")
    void shouldAllowAdminToUpdateAnyPost() {
        // Arrange
        Post otherUsersPost = Post.builder()
                .title("Other user's post")
                .content("Other user's content")
                .status(PostStatus.PUBLISHED)
                .author(otherUser)
                .category(category)
                .build();

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(otherUsersPost));
        when(categoryRepository.findById(updatePostRequest.categoryId())).thenReturn(Optional.of(category));
        when(tagRepository.findByNameIgnoreCase("spring")).thenReturn(Optional.of(tag));
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(Tag.builder().name("java").build()));
        when(tagRepository.findByNameIgnoreCase("testing"))
                .thenReturn(Optional.of(Tag.builder().name("testing").build()));
        when(postRepository.save(otherUsersPost)).thenReturn(otherUsersPost);
        when(postMapper.toResponse(otherUsersPost)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.updatePost(adminUser.getUsername(), postId, updatePostRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);

        verify(userRepository, times(1)).findByUsername(adminUser.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(otherUsersPost);
    }

    @Test
    @DisplayName("shouldAllowModeratorToUpdateAnyPost")
    void shouldAllowModeratorToUpdateAnyPost() {
        // Arrange
        Post otherUsersPost = Post.builder()
                .title("Other user's post")
                .content("Other user's content")
                .status(PostStatus.PUBLISHED)
                .author(otherUser)
                .category(category)
                .build();

        when(userRepository.findByUsername(moderatorUser.getUsername())).thenReturn(Optional.of(moderatorUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(otherUsersPost));
        when(categoryRepository.findById(updatePostRequest.categoryId())).thenReturn(Optional.of(category));
        when(tagRepository.findByNameIgnoreCase("spring")).thenReturn(Optional.of(tag));
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(Tag.builder().name("java").build()));
        when(tagRepository.findByNameIgnoreCase("testing"))
                .thenReturn(Optional.of(Tag.builder().name("testing").build()));
        when(postRepository.save(otherUsersPost)).thenReturn(otherUsersPost);
        when(postMapper.toResponse(otherUsersPost)).thenReturn(postResponse);

        // Act
        PostResponse result = postService.updatePost(moderatorUser.getUsername(), postId, updatePostRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(postResponse);

        verify(userRepository, times(1)).findByUsername(moderatorUser.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(otherUsersPost);
    }

    @Test
    @DisplayName("shouldAllowAdminToDeleteAnyPost")
    void shouldAllowAdminToDeleteAnyPost() {
        // Arrange
        Post otherUsersPost = Post.builder()
                .title("Other user's post")
                .content("Other user's content")
                .status(PostStatus.PUBLISHED)
                .author(otherUser)
                .category(category)
                .build();

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(otherUsersPost));

        // Act & Assert
        assertDoesNotThrow(() -> postService.deletePost(adminUser.getUsername(), postId));

        verify(userRepository, times(1)).findByUsername(adminUser.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    @DisplayName("shouldAllowModeratorToDeleteAnyPost")
    void shouldAllowModeratorToDeleteAnyPost() {
        // Arrange
        Post otherUsersPost = Post.builder()
                .title("Other user's post")
                .content("Other user's content")
                .status(PostStatus.PUBLISHED)
                .author(otherUser)
                .category(category)
                .build();

        when(userRepository.findByUsername(moderatorUser.getUsername())).thenReturn(Optional.of(moderatorUser));
        when(postRepository.findById(postId)).thenReturn(Optional.of(otherUsersPost));

        // Act & Assert
        assertDoesNotThrow(() -> postService.deletePost(moderatorUser.getUsername(), postId));

        verify(userRepository, times(1)).findByUsername(moderatorUser.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }
}
