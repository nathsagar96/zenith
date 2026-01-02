package com.zenith.services;

import static com.zenith.enums.PostStatus.PUBLISHED;

import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.entities.Category;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import com.zenith.entities.User;
import com.zenith.enums.PostStatus;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public static List<String> ALLOWED_SORT_FIELDS = List.of("title", "createdat", "updatedat");

    public void validateSortParams(String sortBy, String sortDirection) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new ValidationException("Invalid sort field: " + sortBy);
        }
        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            throw new ValidationException("Invalid sort direction: " + sortDirection);
        }
    }

    public PageResponse<PostResponse> getPublishedPosts(UUID categoryId, String tag, Pageable pageable) {
        Page<Post> posts;

        if (categoryId != null) {
            posts = postRepository.findByCategoryId(categoryId, pageable);
        } else if (tag != null && !tag.isBlank()) {
            posts = postRepository.findByTagsName(tag, pageable);
        } else {
            posts = postRepository.findPublished(pageable);
        }
        return buildPageResponse(posts);
    }

    public PageResponse<PostResponse> getMyPosts(String username, PostStatus status, Pageable pageable) {
        User author = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));

        Page<Post> posts;

        if (status != null) {
            posts = postRepository.findByAuthorIdAndStatus(author.getId(), status, pageable);
        } else {
            posts = postRepository.findByAuthorId(author.getId(), pageable);
        }
        return buildPageResponse(posts);
    }

    public PageResponse<PostResponse> getPostsByStatus(PostStatus status, Pageable pageable) {
        var posts = postRepository.findByStatus(status, pageable);
        return buildPageResponse(posts);
    }

    public PostResponse getPostById(String username, UUID postId) {
        Post post = findById(postId);

        if (PUBLISHED.equals(post.getStatus())) {
            return postMapper.toResponse(post);
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No authenticated user found"));

        if (!hasAccess(user, post)) {
            throw new ForbiddenException("You are not allowed to view this post");
        }
        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponse createPost(String username, CreatePostRequest request) {
        User author = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));

        Post newPost = postMapper.toEntity(request);
        newPost.setAuthor(author);

        Category category = categoryRepository
                .findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        newPost.setCategory(category);

        Set<Tag> tags = resolveTags(request.tags());
        newPost.setTags(tags);

        return postMapper.toResponse(postRepository.save(newPost));
    }

    @Transactional
    public PostResponse updatePost(String username, UUID postId, UpdatePostRequest request) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No authenticated user found"));

        Post existingPost = findById(postId);
        checkOwnership(user, existingPost);

        if (request.title() != null && !request.title().isBlank()) {
            existingPost.setTitle(request.title());
        }

        if (request.content() != null && !request.content().isBlank()) {
            existingPost.setContent(request.content());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository
                    .findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            existingPost.setCategory(category);
        }

        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = resolveTags(request.tags());
            existingPost.setTags(tags);
        }

        return postMapper.toResponse(postRepository.save(existingPost));
    }

    @Transactional
    public void deletePost(String username, UUID postId) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No authenticated user found"));
        Post exitsingPost = findById(postId);
        checkOwnership(user, exitsingPost);
        postRepository.deleteById(postId);
    }

    @Transactional
    public PostResponse updatePostStatus(UUID postId, PostStatus status) {
        Post existingPost = findById(postId);
        existingPost.setStatus(status);
        return postMapper.toResponse(postRepository.save(existingPost));
    }

    private Post findById(UUID postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    private Set<Tag> resolveTags(Set<String> tagNames) {
        return tagNames.stream()
                .map(name -> tagRepository
                        .findByNameIgnoreCase(name)
                        .orElseGet(() ->
                                tagRepository.save(Tag.builder().name(name).build())))
                .collect(Collectors.toSet());
    }

    private void checkOwnership(User user, Post post) {
        if (!hasAccess(user, post)) {
            throw new ForbiddenException("You are not allowed to edit / delete this post");
        }
    }

    private boolean hasAccess(User user, Post post) {
        return post.getAuthor().getUsername().equals(user.getUsername()) || user.isAdmin() || user.isModerator();
    }

    private PageResponse<PostResponse> buildPageResponse(Page<Post> posts) {
        return PageResponse.<PostResponse>builder()
                .pageNumber(posts.getNumber())
                .totalPages(posts.getTotalPages())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .content(posts.getContent().stream().map(postMapper::toResponse).toList())
                .build();
    }
}
