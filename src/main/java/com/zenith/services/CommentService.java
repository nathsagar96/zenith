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
import com.zenith.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public PageResponse<CommentResponse> getAllComments(Pageable pageable) {
        var comments = commentRepository.findAll(pageable);
        return new PageResponse<>(
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.stream().map(commentMapper::toResponse).toList());
    }

    public PageResponse<CommentResponse> getAllCommentsByPost(Long postId, Pageable pageable) {
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
        var comments = commentRepository.findByAuthorIdAndStatus(authorId, CommentStatus.valueOf(status), pageable);
        return new PageResponse<>(
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.getContent().stream().map(commentMapper::toResponse).toList());
    }

    public CommentResponse getCommentById(Long id) {
        Comment comment = findById(id);
        if (comment.getStatus() != CommentStatus.APPROVED) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        return commentMapper.toResponse(comment);
    }

    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        Post post = postRepository
                .findById(request.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + request.postId()));

        User author = userRepository
                .findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.authorId()));

        Comment newComment = commentMapper.toEntity(request);
        newComment.setPost(post);
        newComment.setAuthor(author);
        Comment savedComment = commentRepository.save(newComment);
        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, UpdateCommentRequest request) {
        Comment existingComment = findById(id);

        existingComment.setContent(request.content());

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toResponse(updatedComment);
    }

    @Transactional
    public void approveComment(long id) {
        Comment comment = findById(id);
        comment.setStatus(CommentStatus.APPROVED);
        commentRepository.save(comment);
    }

    @Transactional
    public void markCommentSpam(long id) {
        Comment comment = findById(id);
        comment.setStatus(CommentStatus.SPAM);
        commentRepository.save(comment);
    }

    @Transactional
    public void archiveComment(Long id) {
        Comment comment = findById(id);
        comment.setStatus(CommentStatus.ARCHIVED);
        commentRepository.save(comment);
    }

    private Comment findById(Long id) {
        return commentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
    }
}
