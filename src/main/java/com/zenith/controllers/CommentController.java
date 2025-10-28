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
        log.info("Received request to get all comments");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<CommentResponse> response = commentService.getAllComments(pageable);
        log.info("Returning {} comments", response.getTotalElements());
        return response;
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
        log.info("Received request to get approved comments for post with id: {}", postId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<CommentResponse> response = commentService.getAllApprovedCommentsByPost(postId, pageable);
        log.info("Returning {} approved comments for post with id: {}", response.getTotalElements(), postId);
        return response;
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
        log.info("Received request to get comments for author {} with status {}", authorId, status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<CommentResponse> response =
                commentService.getAllCommentsByAuthorAndStatus(authorId, status, pageable);
        log.info("Returning {} comments for author {} with status {}", response.getTotalElements(), authorId, status);
        return response;
    }

    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get pending comments", description = "Retrieves a paginated list of pending comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllPendingComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get pending comments");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<CommentResponse> response = commentService.getAllCommentsByStatus(CommentStatus.PENDING, pageable);
        log.info("Returning {} pending comments", response.getTotalElements());
        return response;
    }

    @GetMapping("/approved")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get approved comments", description = "Retrieves a paginated list of approved comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllApprovedComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get approved comments");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<CommentResponse> response =
                commentService.getAllCommentsByStatus(CommentStatus.APPROVED, pageable);
        log.info("Returning {} approved comments", response.getTotalElements());
        return response;
    }

    @GetMapping("/spam")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get spam comments", description = "Retrieves a paginated list of spam comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllSpamComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get spam comments");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<CommentResponse> response = commentService.getAllCommentsByStatus(CommentStatus.SPAM, pageable);
        log.info("Returning {} spam comments", response.getTotalElements());
        return response;
    }

    @GetMapping("/archived")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get archived comments", description = "Retrieves a paginated list of archived comments")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public PageResponse<CommentResponse> getAllArchivedComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get archived comments");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<CommentResponse> response =
                commentService.getAllCommentsByStatus(CommentStatus.ARCHIVED, pageable);
        log.info("Returning {} archived comments", response.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get comment by ID", description = "Retrieves a comment by its ID")
    @ApiResponse(responseCode = "200", description = "Comment retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public CommentResponse getCommentById(@PathVariable("id") Long id) {
        log.info("Received request to get comment with id: {}", id);
        CommentResponse response = commentService.getCommentById(id);
        log.info("Returning comment with id: {}", id);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new comment", description = "Creates a new comment with the provided details")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment details")
    public CommentResponse createComment(@Valid @RequestBody CreateCommentRequest request) {
        log.info("Received request to create comment for post with id: {}", request.postId());
        CommentResponse response = commentService.createComment(request);
        log.info("Comment created successfully with id: {}", response.id());
        return response;
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
        log.info("Received request to update comment with id: {}", id);
        CommentResponse response = commentService.updateComment(id, request);
        log.info("Comment updated successfully with id: {}", id);
        return response;
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a comment", description = "Approves a comment by its ID")
    @ApiResponse(responseCode = "204", description = "Comment approved successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public void approveComment(@PathVariable("id") Long id) {
        log.info("Received request to approve comment with id: {}", id);
        commentService.approveComment(id);
        log.info("Comment approved successfully with id: {}", id);
    }

    @PatchMapping("/{id}/spam")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark a comment as spam", description = "Marks a comment as spam by its ID")
    @ApiResponse(responseCode = "204", description = "Comment marked as spam successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public void markSpam(@PathVariable("id") Long id) {
        log.info("Received request to mark comment as spam with id: {}", id);
        commentService.markSpam(id);
        log.info("Comment marked as spam successfully with id: {}", id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id)")
    @Operation(summary = "Archive a comment", description = "Archives a comment by its ID")
    @ApiResponse(responseCode = "204", description = "Comment archived successfully")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public void archiveComment(@PathVariable("id") Long id) {
        log.info("Received request to archive comment with id: {}", id);
        commentService.archiveComment(id);
        log.info("Comment archived successfully with id: {}", id);
    }
}
