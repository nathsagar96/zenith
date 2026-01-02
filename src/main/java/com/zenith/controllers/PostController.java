package com.zenith.controllers;

import com.zenith.dtos.requests.CreatePostRequest;
import com.zenith.dtos.requests.UpdatePostRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.enums.PostStatus;
import com.zenith.security.SecurityUser;
import com.zenith.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Posts", description = "Post management operations")
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Get published posts",
            description = "Retrieve a paginated list of published posts with optional category and tag filtering",
            parameters = {
                @Parameter(
                        name = "page",
                        description = "Page number (0-based index)",
                        schema = @Schema(defaultValue = "0", minimum = "0")),
                @Parameter(
                        name = "size",
                        description = "Page size",
                        schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100")),
                @Parameter(name = "sortBy", description = "Field to sort by (e.g., title, createdAt, updatedAt)"),
                @Parameter(
                        name = "sortDirection",
                        description = "Sort direction (ASC or DESC)",
                        schema = @Schema(allowableValues = {"ASC", "DESC"})),
                @Parameter(name = "categoryId", description = "Optional category ID to filter by"),
                @Parameter(name = "tag", description = "Optional tag to filter by"),
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PageResponse.class)))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<PostResponse> getPublishedPosts(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String tag) {
        postService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return postService.getPublishedPosts(categoryId, tag, pageable);
    }

    @Operation(
            summary = "Get post by ID",
            description = "Retrieve a specific post by its ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponse.class)))
            })
    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostById(
            @Parameter(description = "ID of the post to retrieve", required = true) @PathVariable("postId") UUID postId,
            @AuthenticationPrincipal SecurityUser user) {
        return postService.getPostById(user.getUsername(), postId);
    }

    @Operation(
            summary = "Get current user's posts",
            description = "Retrieve a paginated list of posts for the authenticated user",
            parameters = {
                @Parameter(
                        name = "page",
                        description = "Page number (0-based index)",
                        schema = @Schema(defaultValue = "0", minimum = "0")),
                @Parameter(
                        name = "size",
                        description = "Page size",
                        schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100")),
                @Parameter(name = "sortBy", description = "Field to sort by (e.g., title, createdAt, updatedAt)"),
                @Parameter(
                        name = "sortDirection",
                        description = "Sort direction (ASC or DESC)",
                        schema = @Schema(allowableValues = {"ASC", "DESC"})),
                @Parameter(name = "status", description = "Optional status to filter by")
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PageResponse.class)))
            })
    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<PostResponse> getMyPosts(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(name = "status", required = false) PostStatus status,
            @AuthenticationPrincipal SecurityUser user) {
        postService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return postService.getMyPosts(user.getUsername(), status, pageable);
    }

    @Operation(
            summary = "Create a new post",
            description = "Create a new post",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Post created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponse.class)))
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(
            @Valid @RequestBody CreatePostRequest request, @AuthenticationPrincipal SecurityUser user) {
        return postService.createPost(user.getUsername(), request);
    }

    @Operation(
            summary = "Update a post",
            description = "Update an existing post by its ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Post updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponse.class)))
            })
    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse updatePost(
            @Parameter(description = "ID of the post to update", required = true) @PathVariable("postId") UUID postId,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return postService.updatePost(user.getUsername(), postId, request);
    }

    @Operation(
            summary = "Delete a post",
            description = "Delete a post by its ID",
            responses = {@ApiResponse(responseCode = "204", description = "Post deleted successfully")})
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @Parameter(description = "ID of the post to delete", required = true) @PathVariable("postId") UUID postId,
            @AuthenticationPrincipal SecurityUser user) {
        postService.deletePost(user.getUsername(), postId);
    }
}
