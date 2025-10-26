package com.zenith.controllers;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.enums.CommentStatus;
import com.zenith.services.CommentService;
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
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "APIs for managing comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all comments", description = "Retrieves a paginated list of all comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return commentService.getAllComments(pageable);
    }

    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get approved comments by post",
            description = "Retrieves a paginated list of approved comments for a specific post")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllApprovedCommentsByPost(
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllApprovedCommentsByPost(postId, pageable);
    }

    @GetMapping("/author/{authorId}/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get comments by author and status",
            description = "Retrieves a paginated list of comments by author and status")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllCommentsByAuthorAndStatus(
            @PathVariable("authorId") Long authorId,
            @PathVariable("status") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByAuthorAndStatus(authorId, status, pageable);
    }

    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get pending comments", description = "Retrieves a paginated list of pending comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllPendingComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.PENDING, pageable);
    }

    @GetMapping("/approved")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get approved comments", description = "Retrieves a paginated list of approved comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllApprovedComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.APPROVED, pageable);
    }

    @GetMapping("/spam")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get spam comments", description = "Retrieves a paginated list of spam comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllSpamComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.SPAM, pageable);
    }

    @GetMapping("/archived")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get archived comments", description = "Retrieves a paginated list of archived comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllArchivedComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.ARCHIVED, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get comment by ID", description = "Retrieves a comment by its ID")
    @ApiResponse(responseCode = "200", description = "Comment retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public CommentResponse getCommentById(@PathVariable("id") Long id) {
        return commentService.getCommentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new comment", description = "Creates a new comment with the provided details")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment details")
    public CommentResponse createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id)")
    @Operation(summary = "Update a comment", description = "Updates an existing comment with the provided details")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment details")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public CommentResponse updateComment(
            @PathVariable("id") Long id, @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(id, request);
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a comment", description = "Approves a comment by its ID")
    @ApiResponse(responseCode = "204", description = "Comment approved successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public void approveComment(@PathVariable("id") Long id) {
        commentService.approveComment(id);
    }

    @PatchMapping("/{id}/spam")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark a comment as spam", description = "Marks a comment as spam by its ID")
    @ApiResponse(responseCode = "204", description = "Comment marked as spam successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public void markSpam(@PathVariable("id") Long id) {
        commentService.markSpam(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id)")
    @Operation(summary = "Archive a comment", description = "Archives a comment by its ID")
    @ApiResponse(responseCode = "204", description = "Comment archived successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public void archiveComment(@PathVariable("id") Long id) {
        commentService.archiveComment(id);
    }
}
