package com.zenith.controllers;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.security.SecurityUser;
import com.zenith.services.CommentService;
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
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "Comments", description = "Comment management operations")
public class CommentController {
    private final CommentService commentService;

    @Operation(
            summary = "Get comments for a post",
            description = "Retrieve a paginated list of comments for a specific post",
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
                @Parameter(name = "postId", description = "ID of the post to get comments for", required = true)
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
    public PageResponse<CommentResponse> getComments(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @PathVariable("postId") UUID postId) {
        commentService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return commentService.getAllComments(postId, pageable);
    }

    @Operation(
            summary = "Add a new comment",
            description = "Create a new comment on a post",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Comment created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CommentResponse.class)))
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @Parameter(description = "ID of the post to comment on", required = true) @PathVariable("postId")
                    UUID postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return commentService.createComment(user.getUsername(), postId, request);
    }

    @Operation(
            summary = "Update a comment",
            description = "Update an existing comment",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CommentResponse.class)))
            })
    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse updateComment(
            @Parameter(description = "ID of the comment to update", required = true) @PathVariable("commentId")
                    UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        return commentService.updateComment(user.getUsername(), commentId, request);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Delete a comment by its ID",
            responses = {@ApiResponse(responseCode = "204", description = "Comment deleted successfully")})
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @Parameter(description = "ID of the comment to delete", required = true) @PathVariable("commentId")
                    UUID commentId,
            @AuthenticationPrincipal SecurityUser user) {
        commentService.deleteComment(user.getUsername(), commentId);
    }
}
