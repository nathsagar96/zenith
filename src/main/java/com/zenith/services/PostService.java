package com.zenith.services;

import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.PostStatus;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.PostMapper;
import com.zenith.repositories.CategoryRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.TagRepository;
import com.zenith.repositories.UserRepository;
import com.zenith.utils.SlugUtils;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PageResponse<PostResponse> getAllPosts(Pageable pageable) {
        var posts = postRepository.findAll(pageable);
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.stream().map(postMapper::toResponse).toList());
    }

    public PageResponse<PostResponse> getAllPublishedPosts(Pageable pageable) {
        var posts = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
        return new PageResponse<>(
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.stream().map(postMapper::toResponse).toList());
    }

    public PageResponse<PostResponse> getAllPostsByAuthorAndStatus(Long authorId, String status, Pageable pageable) {
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
        Post post = findById(id);
        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        User author = userRepository
                .findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.authorId()));

        Post newPost = postMapper.toEntity(request);
        newPost.setAuthor(author);

        var categories = categoryRepository.findAllById(request.categoryIds());
        if (categories.size() != request.categoryIds().size()) {
            throw new ResourceNotFoundException("One or more categories not found");
        }
        newPost.setCategories(new HashSet<>(categories));

        var tags = tagRepository.findAllById(request.tagIds());
        if (tags.size() != request.tagIds().size()) {
            throw new ResourceNotFoundException("One or more tags not found");
        }
        newPost.setTags(new HashSet<>(tags));

        Post createdPost = postRepository.save(newPost);
        String slug = SlugUtils.generateUniqueSlug(request.title(), createdPost.getId());
        createdPost.setSlug(slug);
        return postMapper.toResponse(postRepository.save(createdPost));
    }

    @Transactional
    public PostResponse updatePost(Long id, UpdatePostRequest request) {
        Post existingPost = findById(id);

        if (request.title() != null && !request.title().isBlank()) {
            existingPost.setTitle(request.title());
            String slug = SlugUtils.generateUniqueSlug(request.title(), existingPost.getId());
            existingPost.setSlug(slug);
        }

        if (request.content() != null && !request.content().isBlank()) {
            existingPost.setContent(request.content());
        }

        if (request.categoryIds() != null && !request.categoryIds().isEmpty()) {
            var categories = categoryRepository.findAllById(request.categoryIds());
            if (categories.size() != request.categoryIds().size()) {
                throw new ResourceNotFoundException("One or more categories not found");
            }
            existingPost.setCategories(new HashSet<>(categories));
        }

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            var tags = tagRepository.findAllById(request.tagIds());
            if (tags.size() != request.tagIds().size()) {
                throw new ResourceNotFoundException("One or more tags not found");
            }
            existingPost.setTags(new HashSet<>(tags));
        }

        Post updatedPost = postRepository.save(existingPost);
        return postMapper.toResponse(updatedPost);
    }

    @Transactional
    public void publishPost(long id) {
        Post post = findById(id);
        post.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post);
    }

    @Transactional
    public void archivePost(Long id) {
        Post post = findById(id);
        post.setStatus(PostStatus.ARCHIVED);
        postRepository.save(post);
    }

    private Post findById(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }
}
