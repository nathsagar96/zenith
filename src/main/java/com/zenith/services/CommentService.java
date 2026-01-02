package com.zenith.services;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.PostStatus;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.UnauthorizedException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.CommentMapper;
import com.zenith.repositories.CommentRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public static List<String> ALLOWED_SORT_FIELDS = List.of("createdat", "updatedat");

    public void validateSortParams(String sortBy, String sortDirection) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new ValidationException("Invalid sort field: " + sortBy);
        }
        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            throw new ValidationException("Invalid sort direction: " + sortDirection);
        }
    }

    public PageResponse<CommentResponse> getAllComments(UUID postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }

        var comments = commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable);
        return buildPageResponse(comments);
    }

    public PageResponse<CommentResponse> getCommentsByStatus(CommentStatus status, Pageable pageable) {
        var comments = commentRepository.findByStatus(status, pageable);
        return buildPageResponse(comments);
    }

    @Transactional
    public CommentResponse createComment(String username, UUID postId, CreateCommentRequest request) {
        User author = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("No authenticated user found"));

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new ValidationException("Cannot comment on unpublished post");
        }

        Comment newComment = commentMapper.toEntity(request);
        newComment.setPost(post);
        newComment.setAuthor(author);

        return commentMapper.toResponse(commentRepository.save(newComment));
    }

    @Transactional
    public CommentResponse updateComment(String username, UUID commentId, UpdateCommentRequest request) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No authenticated user found"));
        Comment existingComment = findById(commentId);
        checkOwnership(user, existingComment);
        existingComment.setContent(request.content());

        return commentMapper.toResponse(commentRepository.save(existingComment));
    }

    @Transactional
    public void deleteComment(String username, UUID commentId) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No authenticated user found"));
        Comment existingComment = findById(commentId);
        checkOwnership(user, existingComment);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentResponse updateCommentStatus(UUID commentId, CommentStatus status) {
        Comment existingComment = findById(commentId);
        existingComment.setStatus(status);
        return commentMapper.toResponse(commentRepository.save(existingComment));
    }

    private Comment findById(UUID commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    private void checkOwnership(User user, Comment comment) {
        if (!comment.getAuthor().getUsername().equals(user.getUsername()) && !user.isAdmin() && !user.isModerator()) {
            throw new ForbiddenException("You are not allowed to edit / delete this comment");
        }
    }

    private PageResponse<CommentResponse> buildPageResponse(Page<Comment> comments) {
        return PageResponse.<CommentResponse>builder()
                .pageNumber(comments.getNumber())
                .totalPages(comments.getTotalPages())
                .pageSize(comments.getSize())
                .totalElements(comments.getTotalElements())
                .content(comments.getContent().stream()
                        .map(commentMapper::toResponse)
                        .toList())
                .build();
    }
}
