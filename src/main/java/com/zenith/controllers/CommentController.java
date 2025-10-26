package com.zenith.controllers;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.enums.CommentStatus;
import com.zenith.services.CommentService;
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
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
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
    public PageResponse<CommentResponse> getAllPendingComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.PENDING, pageable);
    }

    @GetMapping("/approved")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CommentResponse> getAllApprovedComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.APPROVED, pageable);
    }

    @GetMapping("/spam")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CommentResponse> getAllSpamComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.SPAM, pageable);
    }

    @GetMapping("/archived")
    public PageResponse<CommentResponse> getAllArchivedComments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentService.getAllCommentsByStatus(CommentStatus.ARCHIVED, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse getCommentById(@PathVariable("id") Long id) {
        return commentService.getCommentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id)")
    public CommentResponse updateComment(
            @PathVariable("id") Long id, @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(id, request);
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void approveComment(@PathVariable("id") Long id) {
        commentService.approveComment(id);
    }

    @PatchMapping("/{id}/spam")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void markSpam(@PathVariable("id") Long id) {
        commentService.markSpam(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id)")
    public void archiveComment(@PathVariable("id") Long id) {
        commentService.archiveComment(id);
    }
}
