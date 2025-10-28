package com.zenith.services;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.CommentStatus;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.CommentMapper;
import com.zenith.repositories.CommentRepository;
import com.zenith.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;

    public PageResponse<CommentResponse> getAllComments(Pageable pageable) {
        log.info("Fetching all comments");
        var comments = commentRepository.findAll(pageable);
        return new PageResponse<>(
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.stream().map(commentMapper::toResponse).toList());
    }

    public PageResponse<CommentResponse> getAllApprovedCommentsByPost(Long postId, Pageable pageable) {
        log.info("Fetching approved comments for post with id: {}", postId);
        var comments = commentRepository.findApprovedByPostId(postId, pageable);
        return new PageResponse<>(
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.getContent().stream().map(commentMapper::toResponse).toList());
    }

    public PageResponse<CommentResponse> getAllCommentsByAuthorAndStatus(
            Long authorId, String status, Pageable pageable) {
        log.info("Fetching comments for author {} with status {}", authorId, status);
        var comments = commentRepository.findByAuthorIdAndStatus(authorId, CommentStatus.valueOf(status), pageable);
        return new PageResponse<>(
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.getContent().stream().map(commentMapper::toResponse).toList());
    }

    public PageResponse<CommentResponse> getAllCommentsByStatus(CommentStatus status, Pageable pageable) {
        Long authorId = userService.getCurrentUser().getId();
        log.info("Fetching comments for current user {} with status {}", authorId, status);
        return getAllCommentsByAuthorAndStatus(authorId, String.valueOf(status), pageable);
    }

    public CommentResponse getCommentById(Long id) {
        log.info("Fetching comment with id: {}", id);
        Comment comment = findById(id);
        if (comment.getStatus() != CommentStatus.APPROVED) {
            log.warn("Comment not found or not approved with id: {}", id);
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        return commentMapper.toResponse(comment);
    }

    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        log.info("Creating comment for post with id: {}", request.postId());

        Post post = postRepository
                .findById(request.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + request.postId()));

        User author = userService.getCurrentUser();

        Comment newComment = commentMapper.toEntity(request);
        newComment.setPost(post);
        newComment.setAuthor(author);
        Comment savedComment = commentRepository.save(newComment);
        log.info("Comment created successfully with id: {}", savedComment.getId());
        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, UpdateCommentRequest request) {
        log.info("Updating comment with id: {}", id);

        Comment existingComment = findById(id);

        existingComment.setContent(request.content());

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment updated successfully with id: {}", updatedComment.getId());
        return commentMapper.toResponse(updatedComment);
    }

    @Transactional
    public void approveComment(Long id) {
        log.info("Approving comment with id: {}", id);

        Comment comment = findById(id);
        comment.setStatus(CommentStatus.APPROVED);
        commentRepository.save(comment);
        log.info("Comment approved successfully with id: {}", id);
    }

    @Transactional
    public void markSpam(Long id) {
        log.info("Marking comment as spam with id: {}", id);

        Comment comment = findById(id);
        comment.setStatus(CommentStatus.SPAM);
        commentRepository.save(comment);
        log.info("Comment marked as spam successfully with id: {}", id);
    }

    @Transactional
    public void archiveComment(Long id) {
        log.info("Archiving comment with id: {}", id);

        Comment comment = findById(id);

        comment.setStatus(CommentStatus.ARCHIVED);
        commentRepository.save(comment);
        log.info("Comment archived successfully with id: {}", id);
    }

    private Comment findById(Long id) {
        return commentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
    }

    public boolean isCommentAuthor(Long commentId) {
        Long currentUserId = userService.getCurrentUser().getId();
        Comment comment = findById(commentId);
        return comment.getAuthor().getId().equals(currentUserId);
    }
}
