package com.zenith.controllers;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CommentController {
    private CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CommentResponse> getAllComments(Pageable pageable) {
        return commentService.getAllComments(pageable);
    }

    @GetMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CommentResponse> getAllCommentsByPost(@PathVariable("postId") Long postId, Pageable pageable) {
        return commentService.getAllCommentsByPost(postId, pageable);
    }

    @GetMapping("/author/{authorId}/status/{status}")
    public PageResponse<CommentResponse> getAllCommentsByAuthorAndStatus(
            @PathVariable("authorId") Long authorId, @PathVariable("status") String status, Pageable pageable) {
        return commentService.getAllCommentsByAuthorAndStatus(authorId, status, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse getCommentById(Long id) {
        return commentService.getCommentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse updateComment(
            @PathVariable("id") Long id, @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(id, request);
    }

    @PatchMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveComment(@PathVariable("id") Long id) {
        commentService.approveComment(id);
    }

    @PatchMapping("/{id}/spam")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markSpam(@PathVariable("id") Long id) {
        commentService.markSpam(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("id") Long id) {
        commentService.archiveComment(id);
    }
}
