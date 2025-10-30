package com.zenith.controllers;

import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.PostResponse;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.PostStatus;
import com.zenith.services.CommentService;
import com.zenith.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin operations for managing posts and comments")
public class AdminController {
    private final PostService postService;
    private final CommentService commentService;

    @Operation(
            summary = "Get posts by status",
            description = "Retrieve a paginated list of posts filtered by status",
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
                @Parameter(name = "status", description = "Status of the posts to filter by", required = true),
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
    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<PostResponse> getPostsByStatus(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "status") PostStatus status) {
        postService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return postService.getPostsByStatus(status, pageable);
    }

    @Operation(
            summary = "Update post status",
            description = "Update the status of a post",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Post status updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PostResponse.class)))
            })
    @PatchMapping("/posts/{postId}/status")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse updateStatus(
            @Parameter(description = "ID of the post to update", required = true) @PathVariable("postId") UUID postId,
            @Parameter(description = "Status to set", required = true) @RequestParam("status") PostStatus status) {
        return postService.updatePostStatus(postId, status);
    }

    @Operation(
            summary = "Get comments by status",
            description = "Retrieve a paginated list of comments filtered by status with sorting options",
            parameters = {
                @Parameter(
                        name = "page",
                        description = "Page number (0-based index)",
                        schema = @Schema(defaultValue = "0", minimum = "0")),
                @Parameter(
                        name = "size",
                        description = "Page size",
                        schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100")),
                @Parameter(name = "sortBy", description = "Field to sort by (e.g., createdAt, updatedAt)"),
                @Parameter(
                        name = "sortDirection",
                        description = "Sort direction (ASC or DESC)",
                        schema = @Schema(allowableValues = {"ASC", "DESC"})),
                @Parameter(name = "status", description = "Status of the comments to filter by", required = true)
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
    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CommentResponse> getCommentsByStatus(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam("status") CommentStatus status) {
        commentService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);

        return commentService.getCommentsByStatus(status, pageable);
    }

    @Operation(
            summary = "Update comment status",
            description = "Update the status of a specific comment",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful update",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CommentResponse.class)))
            })
    @PatchMapping("/comments/{commentId}/status")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse updateCommentStatus(
            @Parameter(description = "ID of the comment to update", required = true) @PathVariable("commentId")
                    UUID commentId,
            @Parameter(description = "Status to set", required = true) @RequestParam("status") CommentStatus status) {
        return commentService.updateCommentStatus(commentId, status);
    }

    @Operation(
            summary = "Bulk update comment statuses",
            description = "Update the status of multiple comments at once",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful bulk update",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CommentResponse.class, type = "array")))
            })
    @PatchMapping("/comments/bulk-status")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> updateCommentsStatus(
            @Parameter(description = "List of comment IDs to update", required = true) @RequestBody
                    List<UUID> commentIds,
            @Parameter(description = "Status to set", required = true) @RequestParam("status") CommentStatus status) {
        return commentService.bulkUpdateCommentStatus(commentIds, status);
    }
}
