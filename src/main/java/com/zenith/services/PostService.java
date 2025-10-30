package com.zenith.services;

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
import com.zenith.utils.SecurityUtils;
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

    public static List<String> ALLOWED_SORT_FIELDS = List.of("title", "createdAt", "updatedAt");

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

    public PageResponse<PostResponse> getMyPosts(PostStatus status, Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
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

    public PostResponse getPostById(UUID postId) {
        Post post = findById(postId);

        if (post.getStatus() != PostStatus.PUBLISHED && !hasAccess(post)) {
            throw new ForbiddenException("You do not have permission to view this post");
        }

        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request, String username) {
        Post newPost = postMapper.toEntity(request);

        User author = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));
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
    public PostResponse updatePost(UUID postId, UpdatePostRequest request) {
        Post existingPost = findById(postId);
        checkOwnership(existingPost);

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
    public void deletePost(UUID postId) {
        Post exitsingPost = findById(postId);
        checkOwnership(exitsingPost);
        exitsingPost.setStatus(PostStatus.ARCHIVED);

        postRepository.save(exitsingPost);
    }

    @Transactional
    public PostResponse publishPost(UUID postId) {
        Post existingPost = findById(postId);
        checkOwnership(existingPost);
        existingPost.setStatus(PostStatus.PUBLISHED);

        return postMapper.toResponse(postRepository.save(existingPost));
    }

    @Transactional
    public PostResponse updatePostStatus(UUID postId, PostStatus status) {
        if (!SecurityUtils.isAdmin()) {
            throw new ForbiddenException("Only admin can change post status");
        }

        Post existingPost = findById(postId);
        existingPost.setStatus(status);

        return postMapper.toResponse(postRepository.save(existingPost));
    }

    public boolean isOwner(UUID postId, String username) {
        return postRepository
                .findById(postId)
                .map(p -> p.getAuthor().getUsername().equals(username))
                .orElse(false);
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

    private void checkOwnership(Post post) {
        String username = SecurityUtils.getCurrentUsername();

        if (!post.getAuthor().getUsername().equals(username) && !SecurityUtils.isAdmin()) {
            throw new ForbiddenException("You can only edit your own posts");
        }
    }

    private boolean hasAccess(Post post) {
        String username = SecurityUtils.getCurrentUsername();

        return post.getAuthor().getUsername().equals(username) || SecurityUtils.isAdmin();
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
