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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPosts(pageable);
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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPublishedPosts(pageable);
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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPublishedPostsByAuthor(authorId, pageable);
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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPostsByAuthorAndStatus(authorId, status, pageable);
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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPostsByStatus(PostStatus.DRAFT, pageable);
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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPostsByStatus(PostStatus.PUBLISHED, pageable);
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
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.getAllPostsByStatus(PostStatus.ARCHIVED, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get post by ID", description = "Retrieves a post by its ID")
    @ApiResponse(responseCode = "200", description = "Post retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public PostResponse getPostById(@PathVariable("id") Long id) {
        return postService.getPostById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new post", description = "Creates a new post with the provided details")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid post details")
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostAuthor(#id)")
    @Operation(summary = "Update a post", description = "Updates an existing post with the provided details")
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid post details")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public PostResponse updatePost(@PathVariable("id") Long id, @Valid @RequestBody UpdatePostRequest request) {
        return postService.updatePost(id, request);
    }

    @PatchMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostAuthor(#id)")
    @Operation(summary = "Publish a post", description = "Publishes a post by its ID")
    @ApiResponse(responseCode = "204", description = "Post published successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public void publishPost(@PathVariable("id") Long id) {
        postService.publishPost(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @postService.isPostAuthor(#id)")
    @Operation(summary = "Archive a post", description = "Archive a post by its ID")
    @ApiResponse(responseCode = "204", description = "Post archived successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public void archivePost(@PathVariable("id") Long id) {
        postService.archivePost(id);
    }
}
