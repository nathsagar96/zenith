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
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.PostMapper;
import com.zenith.repositories.CategoryRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.TagRepository;
import com.zenith.utils.SlugUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    public PageResponse<PostResponse> getAllPosts(Pageable pageable) {
        log.info("Fetching all posts");
        var posts = postRepository.findAll(pageable);
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.stream().map(postMapper::toResponse).toList());
    }

    public PageResponse<PostResponse> getAllPublishedPosts(Pageable pageable) {
        log.info("Fetching all published posts");
        var posts = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.stream().map(postMapper::toResponse).toList());
    }

    public PageResponse<PostResponse> getAllPublishedPostsByAuthor(Long authorId, Pageable pageable) {
        log.info("Fetching published posts for author with id: {}", authorId);
        var posts = postRepository.findByAuthorIdAndStatus(authorId, PostStatus.PUBLISHED, pageable);
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.stream().map(postMapper::toResponse).toList());
    }

    public PageResponse<PostResponse> getAllPostsByStatus(PostStatus status, Pageable pageable) {
        Long currentUserId = userService.getCurrentUser().getId();
        log.info("Fetching posts for user {} with status {}", currentUserId, status);
        return getAllPostsByAuthorAndStatus(currentUserId, String.valueOf(status), pageable);
    }

    public PageResponse<PostResponse> getAllPostsByAuthorAndStatus(Long authorId, String status, Pageable pageable) {
        log.info("Fetching posts for author {} with status {}", authorId, status);
        var postStatus = PostStatus.valueOf(status);
        var posts = postRepository.findByAuthorIdAndStatus(authorId, postStatus, pageable);
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.stream().map(postMapper::toResponse).toList());
    }

    public PostResponse getPostById(Long id) {
        log.info("Fetching post with id: {}", id);
        Post post = findById(id);
        if (post.getStatus() != PostStatus.PUBLISHED) {
            log.warn("Post not found or not published with id: {}", id);
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        log.info("Creating post with title: {}", request.title());

        User author = userService.getCurrentUser();

        Post newPost = postMapper.toEntity(request);
        newPost.setAuthor(author);

        String categoryName = request.category();
        Category category = categoryRepository
                .findByNameIgnoreCase(categoryName)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with name: '" + request.category()));
        newPost.setCategory(category);

        Set<Tag> tags = new HashSet<>();
        for (String tagName : request.tags()) {
            Tag tag = tagRepository.findByNameIgnoreCase(tagName).orElseGet(() -> {
                Tag newTag = Tag.builder().name(tagName).build();
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }
        newPost.setTags(tags);

        String slug = SlugUtils.generateSlug(request.title());
        newPost.setSlug(slug);

        Post createdPost = postRepository.save(newPost);
        log.info("Post created successfully with id: {}", createdPost.getId());

        return postMapper.toResponse(createdPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, UpdatePostRequest request) {
        log.info("Updating post with id: {}", id);

        Post existingPost = findById(id);

        if (request.title() != null && !request.title().isBlank()) {
            existingPost.setTitle(request.title());
            String slug = SlugUtils.generateSlug(request.title());
            existingPost.setSlug(slug);
        }

        if (request.content() != null && !request.content().isBlank()) {
            existingPost.setContent(request.content());
        }

        if (request.category() != null && !request.category().isBlank()) {
            String categoryName = request.category();
            Category category = categoryRepository
                    .findByNameIgnoreCase(categoryName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found with name: '" + request.category()));
            existingPost.setCategory(category);
        }

        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : request.tags()) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName).orElseGet(() -> {
                    Tag newTag = Tag.builder().name(tagName).build();
                    return tagRepository.save(newTag);
                });
                tags.add(tag);
            }
            existingPost.setTags(tags);
        }

        Post updatedPost = postRepository.save(existingPost);
        log.info("Post updated successfully with id: {}", updatedPost.getId());
        return postMapper.toResponse(updatedPost);
    }

    @Transactional
    public void publishPost(Long id) {
        log.info("Publishing post with id: {}", id);

        Post post = findById(id);
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);
        log.info("Post published successfully with id: {}", id);
    }

    @Transactional
    public void archivePost(Long id) {
        log.info("Archiving post with id: {}", id);

        Post post = findById(id);
        post.setStatus(PostStatus.ARCHIVED);
        postRepository.save(post);
        log.info("Post archived successfully with id: {}", id);
    }

    private Post findById(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }

    public boolean isPostAuthor(Long postId) {
        Long currentUserId = userService.getCurrentUser().getId();
        Post post = findById(postId);
        return post.getAuthor().getId().equals(currentUserId);
    }
}
