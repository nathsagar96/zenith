package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.PostMapper;
import com.zenith.repositories.CategoryRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.TagRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private PostMapper postMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PostResponse postResponse;
    private User user;
    private Category category;
    private Tag tag;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setStatus(PostStatus.PUBLISHED);

        postResponse =
                new PostResponse(1L, "Test Post", "test-post", "Test Content", "PUBLISHED", 1L, null, null, 0, 0, 0);

        user = new User();
        user.setId(1L);
        post.setAuthor(user);

        category = new Category();
        category.setId(1L);

        tag = new Tag();
        tag.setId(1L);
    }

    @Test
    @DisplayName("should get all posts")
    void shouldGetAllPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PageResponse<PostResponse> response = postService.getAllPosts(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(postResponse, response.getContent().getFirst());

        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("should get all published posts")
    void shouldGetAllPublishedPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByStatus(any(PostStatus.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PageResponse<PostResponse> response = postService.getAllPublishedPosts(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(postResponse, response.getContent().getFirst());

        verify(postRepository, times(1)).findByStatus(PostStatus.PUBLISHED, pageable);
    }

    @Test
    @DisplayName("should get all published posts by author")
    void shouldGetAllPublishedPostsByAuthor() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByAuthorIdAndStatus(anyLong(), any(PostStatus.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PageResponse<PostResponse> response = postService.getAllPublishedPostsByAuthor(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(postResponse, response.getContent().getFirst());

        verify(postRepository, times(1)).findByAuthorIdAndStatus(1L, PostStatus.PUBLISHED, pageable);
    }

    @Test
    @DisplayName("should get all posts by status")
    void shouldGetAllPostsByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByAuthorIdAndStatus(anyLong(), any(PostStatus.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(userService.getCurrentUser()).thenReturn(user);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PageResponse<PostResponse> response = postService.getAllPostsByStatus(PostStatus.PUBLISHED, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(postResponse, response.getContent().getFirst());

        verify(postRepository, times(1)).findByAuthorIdAndStatus(1L, PostStatus.PUBLISHED, pageable);
    }

    @Test
    @DisplayName("should get all posts by author and status")
    void shouldGetAllPostsByAuthorAndStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(List.of(post), pageable, 1);

        when(postRepository.findByAuthorIdAndStatus(anyLong(), any(PostStatus.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PageResponse<PostResponse> response = postService.getAllPostsByAuthorAndStatus(1L, "PUBLISHED", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(postResponse, response.getContent().getFirst());

        verify(postRepository, times(1)).findByAuthorIdAndStatus(1L, PostStatus.PUBLISHED, pageable);
    }

    @Test
    @DisplayName("should get post by id successfully")
    void shouldGetPostByIdSuccessfully() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PostResponse response = postService.getPostById(1L);

        assertNotNull(response);
        assertEquals(postResponse, response);

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when post not found by id")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundById() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(1L));

        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should create post successfully")
    void shouldCreatePostSuccessfully() {
        CreatePostRequest request = new CreatePostRequest("Test Post", "Test Content", Set.of(1L), Set.of(1L));

        when(postMapper.toEntity(any(CreatePostRequest.class))).thenReturn(post);
        when(categoryRepository.findAllById(anySet())).thenReturn(List.of(category));
        when(tagRepository.findAllById(anySet())).thenReturn(List.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PostResponse response = postService.createPost(request);

        assertNotNull(response);
        assertEquals(postResponse, response);

        verify(postRepository, times(2)).save(any(Post.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when category not found for post creation")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundForPostCreation() {
        CreatePostRequest request = new CreatePostRequest("Test Post", "Test Content", Set.of(2L), Set.of(1L));

        when(postMapper.toEntity(any(CreatePostRequest.class))).thenReturn(post);

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(request));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when tag not found for post creation")
    void shouldThrowResourceNotFoundExceptionWhenTagNotFoundForPostCreation() {
        CreatePostRequest request = new CreatePostRequest("Test Post", "Test Content", Set.of(1L), Set.of(2L));

        when(postMapper.toEntity(any(CreatePostRequest.class))).thenReturn(post);

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(request));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("should update post successfully")
    void shouldUpdatePostSuccessfully() {
        UpdatePostRequest request = new UpdatePostRequest("Updated Post", "Updated Content", Set.of(1L), Set.of(1L));

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(categoryRepository.findAllById(anySet())).thenReturn(List.of(category));
        when(tagRepository.findAllById(anySet())).thenReturn(List.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponse(any(Post.class))).thenReturn(postResponse);

        PostResponse response = postService.updatePost(1L, request);

        assertNotNull(response);
        assertEquals(postResponse, response);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when post not found for update")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForUpdate() {
        UpdatePostRequest request = new UpdatePostRequest("Updated Post", "Updated Content", Set.of(1L), Set.of(1L));

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(1L, request));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("should publish post successfully")
    void shouldPublishPostSuccessfully() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.publishPost(1L));

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("should archive post successfully")
    void shouldArchivePostSuccessfully() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.archivePost(1L));

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("should return true when user is post author")
    void shouldReturnTrueWhenUserIsPostAuthor() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.getCurrentUser()).thenReturn(user);

        boolean result = postService.isPostAuthor(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("should return false when user is not post author")
    void shouldReturnFalseWhenUserIsNotPostAuthor() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.getCurrentUser()).thenReturn(differentUser);

        boolean result = postService.isPostAuthor(1L);

        assertFalse(result);
    }
}
