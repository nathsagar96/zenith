package com.zenith.controllers;

import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.enums.PostStatus;
import com.zenith.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Posts", description = "APIs for managing posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all posts", description = "Retrieves a paginated list of all posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get all posts");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPosts(pageable);
        log.info("Returning {} posts", response.getTotalElements());
        return response;
    }

    @GetMapping("/public")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all public posts", description = "Retrieves a paginated list of all public posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllPublicPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get all public posts");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPublishedPosts(pageable);
        log.info("Returning {} public posts", response.getTotalElements());
        return response;
    }

    @GetMapping("/author/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get public posts by author",
            description = "Retrieves a paginated list of public posts by author")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllPublicPostsByAuthor(
            @PathVariable("authorId") Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get public posts for author with id: {}", authorId);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPublishedPostsByAuthor(authorId, pageable);
        log.info("Returning {} public posts for author with id: {}", response.getTotalElements(), authorId);
        return response;
    }

    @GetMapping("/author/{authorId}/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get posts by author and status",
            description = "Retrieves a paginated list of posts by author and status")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllPostsByAuthorAndStatus(
            @PathVariable("authorId") Long authorId,
            @PathVariable("status") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get posts for author {} with status {}", authorId, status);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPostsByAuthorAndStatus(authorId, status, pageable);
        log.info("Returning {} posts for author {} with status {}", response.getTotalElements(), authorId, status);
        return response;
    }

    @GetMapping("/drafts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get draft posts", description = "Retrieves a paginated list of draft posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllDraftPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get draft posts");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPostsByStatus(PostStatus.DRAFT, pageable);
        log.info("Returning {} draft posts", response.getTotalPages());
        return response;
    }

    @GetMapping("/published")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get published posts", description = "Retrieves a paginated list of published posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get published posts");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPostsByStatus(PostStatus.PUBLISHED, pageable);
        log.info("Returning {} published posts", response.getTotalElements());
        return response;
    }

    @GetMapping("/archived")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get archived posts", description = "Retrieves a paginated list of archived posts")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public PageResponse<PostResponse> getAllArchivedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get archived posts");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<PostResponse> response = postService.getAllPostsByStatus(PostStatus.ARCHIVED, pageable);
        log.info("Returning {} archived posts", response.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get post by ID", description = "Retrieves a post by its ID")
    @ApiResponse(responseCode = "200", description = "Post retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public PostResponse getPostById(@PathVariable("id") Long id) {
        log.info("Received request to get post with id: {}", id);
        PostResponse response = postService.getPostById(id);
        log.info("Returning post with id: {}", id);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new post", description = "Creates a new post with the provided details")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid post details")
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        log.info("Received request to create post with title: {}", request.title());
        PostResponse response = postService.createPost(request);
        log.info("Post created successfully with id: {}", response.id());
        return response;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostAuthor(#id)")
    @Operation(summary = "Update a post", description = "Updates an existing post with the provided details")
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid post details")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public PostResponse updatePost(@PathVariable("id") Long id, @Valid @RequestBody UpdatePostRequest request) {
        log.info("Received request to update post with id: {}", id);
        PostResponse response = postService.updatePost(id, request);
        log.info("Post updated successfully with id: {}", id);
        return response;
    }

    @PatchMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostAuthor(#id)")
    @Operation(summary = "Publish a post", description = "Publishes a post by its ID")
    @ApiResponse(responseCode = "204", description = "Post published successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public void publishPost(@PathVariable("id") Long id) {
        log.info("Received request to publish post with id: {}", id);
        postService.publishPost(id);
        log.info("Post published successfully with id: {}", id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostAuthor(#id)")
    @Operation(summary = "Archive a post", description = "Archive a post by its ID")
    @ApiResponse(responseCode = "204", description = "Post archived successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public void archivePost(@PathVariable("id") Long id) {
        log.info("Received request to archive post with id: {}", id);
        postService.archivePost(id);
        log.info("Post archived successfully with id: {}", id);
    }
}
