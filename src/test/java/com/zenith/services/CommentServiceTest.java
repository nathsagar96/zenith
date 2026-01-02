package com.zenith.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.CreateCommentRequest;
import com.zenith.dtos.requests.UpdateCommentRequest;
import com.zenith.dtos.responses.CommentResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.PostStatus;
import com.zenith.enums.RoleType;
import com.zenith.exceptions.ForbiddenException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.UnauthorizedException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.CommentMapper;
import com.zenith.repositories.CommentRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CreateCommentRequest createCommentRequest;
    private UpdateCommentRequest updateCommentRequest;
    private Comment comment;
    private CommentResponse commentResponse;
    private Post post;
    private User user;
    private User adminUser;
    private User moderatorUser;
    private User otherUser;
    private UUID postId;
    private UUID commentId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup common test data
        createCommentRequest = new CreateCommentRequest("This is a test comment");
        updateCommentRequest = new UpdateCommentRequest("This is an updated comment");
        commentId = UUID.randomUUID();
        postId = UUID.randomUUID();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        adminUser = User.builder()
                .username("adminuser")
                .email("admin@example.com")
                .password("password")
                .role(RoleType.ADMIN)
                .build();

        moderatorUser = User.builder()
                .username("moderatoruser")
                .email("moderator@example.com")
                .password("password")
                .role(RoleType.MODERATOR)
                .build();

        otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password")
                .role(RoleType.USER)
                .build();

        post = Post.builder()
                .title("Test Post")
                .content("Test content")
                .status(PostStatus.PUBLISHED)
                .build();

        comment = Comment.builder()
                .content("Test comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(user)
                .build();

        commentResponse =
                new CommentResponse(commentId, "Test comment", CommentStatus.PENDING, postId, user.getId(), null, null);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("shouldValidateSortParamsSuccessfullyWhenValid")
    void shouldValidateSortParamsSuccessfullyWhenValid() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            commentService.validateSortParams("createdat", "asc");
            commentService.validateSortParams("updatedat", "desc");
        });
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenSortFieldIsInvalid")
    void shouldThrowValidationExceptionWhenSortFieldIsInvalid() {
        // Act & Assert
        ValidationException exception =
                assertThrows(ValidationException.class, () -> commentService.validateSortParams("invalidField", "asc"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort field: invalidField");
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenSortDirectionIsInvalid")
    void shouldThrowValidationExceptionWhenSortDirectionIsInvalid() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> commentService.validateSortParams("createdat", "invalidDirection"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort direction: invalidDirection");
    }

    @Test
    @DisplayName("shouldGetAllCommentsSuccessfully")
    void shouldGetAllCommentsSuccessfully() {
        // Arrange
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable))
                .thenReturn(commentPage);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        // Act
        PageResponse<CommentResponse> result = commentService.getAllComments(postId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(commentResponse);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(postRepository, times(1)).existsById(postId);
        verify(commentRepository, times(1)).findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable);
        verify(commentMapper, times(1)).toResponse(comment);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenPostNotFoundForGetAllComments")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForGetAllComments() {
        // Arrange
        when(postRepository.existsById(postId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getAllComments(postId, pageable));

        verify(postRepository, times(1)).existsById(postId);
        verify(commentRepository, never()).findByPostIdAndStatus(any(), any(), any());
    }

    @Test
    @DisplayName("shouldGetCommentsByStatusSuccessfully")
    void shouldGetCommentsByStatusSuccessfully() {
        // Arrange
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));
        when(commentRepository.findByStatus(CommentStatus.PENDING, pageable)).thenReturn(commentPage);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        // Act
        PageResponse<CommentResponse> result = commentService.getCommentsByStatus(CommentStatus.PENDING, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(commentResponse);

        verify(commentRepository, times(1)).findByStatus(CommentStatus.PENDING, pageable);
        verify(commentMapper, times(1)).toResponse(comment);
    }

    @Test
    @DisplayName("shouldCreateCommentSuccessfully")
    void shouldCreateCommentSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(createCommentRequest)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        // Act
        CommentResponse result = commentService.createComment(user.getUsername(), postId, createCommentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(commentResponse);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(commentMapper, times(1)).toEntity(createCommentRequest);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toResponse(comment);
    }

    @Test
    @DisplayName("shouldThrowUnauthorizedExceptionWhenUserNotFoundForCreateComment")
    void shouldThrowUnauthorizedExceptionWhenUserNotFoundForCreateComment() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UnauthorizedException.class,
                () -> commentService.createComment(user.getUsername(), postId, createCommentRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, never()).findById(any());
        verify(commentMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenPostNotFoundForCreateComment")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForCreateComment() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(user.getUsername(), postId, createCommentRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(commentMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenCreatingCommentOnUnpublishedPost")
    void shouldThrowValidationExceptionWhenCreatingCommentOnUnpublishedPost() {
        // Arrange
        Post unpublishedPost = Post.builder()
                .title("Unpublished Post")
                .content("Unpublished content")
                .status(PostStatus.DRAFT)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(unpublishedPost));

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> commentService.createComment(user.getUsername(), postId, createCommentRequest));

        assertThat(exception.getMessage()).isEqualTo("Cannot comment on unpublished post");

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(postId);
        verify(commentMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("shouldUpdateCommentSuccessfully")
    void shouldUpdateCommentSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        // Act
        CommentResponse result = commentService.updateComment(user.getUsername(), commentId, updateCommentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(commentResponse);
        assertThat(comment.getContent()).isEqualTo(updateCommentRequest.content());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toResponse(comment);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenUserNotFoundForUpdateComment")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundForUpdateComment() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateComment(user.getUsername(), commentId, updateCommentRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, never()).findById(any());
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForUpdate")
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForUpdate() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateComment(user.getUsername(), commentId, updateCommentRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldThrowForbiddenExceptionWhenUserNotOwnerForUpdateComment")
    void shouldThrowForbiddenExceptionWhenUserNotOwnerForUpdateComment() {
        // Arrange
        Comment otherUsersComment = Comment.builder()
                .content("Other user's comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(otherUser)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(otherUsersComment));

        // Act & Assert
        assertThrows(
                ForbiddenException.class,
                () -> commentService.updateComment(user.getUsername(), commentId, updateCommentRequest));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldAllowAdminToUpdateAnyComment")
    void shouldAllowAdminToUpdateAnyComment() {
        // Arrange
        Comment otherUsersComment = Comment.builder()
                .content("Other user's comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(otherUser)
                .build();

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(otherUsersComment));
        when(commentRepository.save(otherUsersComment)).thenReturn(otherUsersComment);
        when(commentMapper.toResponse(otherUsersComment)).thenReturn(commentResponse);

        // Act
        CommentResponse result = commentService.updateComment(adminUser.getUsername(), commentId, updateCommentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(commentResponse);

        verify(userRepository, times(1)).findByUsername(adminUser.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(otherUsersComment);
    }

    @Test
    @DisplayName("shouldAllowModeratorToUpdateAnyComment")
    void shouldAllowModeratorToUpdateAnyComment() {
        // Arrange
        Comment otherUsersComment = Comment.builder()
                .content("Other user's comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(otherUser)
                .build();

        when(userRepository.findByUsername(moderatorUser.getUsername())).thenReturn(Optional.of(moderatorUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(otherUsersComment));
        when(commentRepository.save(otherUsersComment)).thenReturn(otherUsersComment);
        when(commentMapper.toResponse(otherUsersComment)).thenReturn(commentResponse);

        // Act
        CommentResponse result =
                commentService.updateComment(moderatorUser.getUsername(), commentId, updateCommentRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(commentResponse);

        verify(userRepository, times(1)).findByUsername(moderatorUser.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(otherUsersComment);
    }

    @Test
    @DisplayName("shouldDeleteCommentSuccessfully")
    void shouldDeleteCommentSuccessfully() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertDoesNotThrow(() -> commentService.deleteComment(user.getUsername(), commentId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenUserNotFoundForDeleteComment")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundForDeleteComment() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class, () -> commentService.deleteComment(user.getUsername(), commentId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, never()).findById(any());
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForDelete")
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForDelete() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class, () -> commentService.deleteComment(user.getUsername(), commentId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldThrowForbiddenExceptionWhenUserNotOwnerForDeleteComment")
    void shouldThrowForbiddenExceptionWhenUserNotOwnerForDeleteComment() {
        // Arrange
        Comment otherUsersComment = Comment.builder()
                .content("Other user's comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(otherUser)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(otherUsersComment));

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> commentService.deleteComment(user.getUsername(), commentId));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldAllowAdminToDeleteAnyComment")
    void shouldAllowAdminToDeleteAnyComment() {
        // Arrange
        Comment otherUsersComment = Comment.builder()
                .content("Other user's comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(otherUser)
                .build();

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(otherUsersComment));

        // Act & Assert
        assertDoesNotThrow(() -> commentService.deleteComment(adminUser.getUsername(), commentId));

        verify(userRepository, times(1)).findByUsername(adminUser.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    @DisplayName("shouldAllowModeratorToDeleteAnyComment")
    void shouldAllowModeratorToDeleteAnyComment() {
        // Arrange
        Comment otherUsersComment = Comment.builder()
                .content("Other user's comment")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(otherUser)
                .build();

        when(userRepository.findByUsername(moderatorUser.getUsername())).thenReturn(Optional.of(moderatorUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(otherUsersComment));

        // Act & Assert
        assertDoesNotThrow(() -> commentService.deleteComment(moderatorUser.getUsername(), commentId));

        verify(userRepository, times(1)).findByUsername(moderatorUser.getUsername());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    @DisplayName("shouldUpdateCommentStatusSuccessfully")
    void shouldUpdateCommentStatusSuccessfully() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);

        // Act
        CommentResponse result = commentService.updateCommentStatus(commentId, CommentStatus.APPROVED);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(commentResponse);
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.APPROVED);

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toResponse(comment);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForStatusUpdate")
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForStatusUpdate() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateCommentStatus(commentId, CommentStatus.APPROVED));

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("shouldHandleEmptyPageWhenNoCommentsFoundByStatus")
    void shouldHandleEmptyPageWhenNoCommentsFoundByStatus() {
        // Arrange
        Page<Comment> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(commentRepository.findByStatus(CommentStatus.REJECTED, pageable)).thenReturn(emptyPage);

        // Act
        PageResponse<CommentResponse> result = commentService.getCommentsByStatus(CommentStatus.REJECTED, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);

        verify(commentRepository, times(1)).findByStatus(CommentStatus.REJECTED, pageable);
        verify(commentMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("shouldHandleEmptyPageWhenNoApprovedCommentsFoundForPost")
    void shouldHandleEmptyPageWhenNoApprovedCommentsFoundForPost() {
        // Arrange
        Page<Comment> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable))
                .thenReturn(emptyPage);

        // Act
        PageResponse<CommentResponse> result = commentService.getAllComments(postId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);

        verify(postRepository, times(1)).existsById(postId);
        verify(commentRepository, times(1)).findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable);
        verify(commentMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("shouldHandleMultipleCommentsInPageResponse")
    void shouldHandleMultipleCommentsInPageResponse() {
        // Arrange
        Comment comment2 = Comment.builder()
                .content("Second comment")
                .status(CommentStatus.APPROVED)
                .post(post)
                .author(user)
                .build();

        CommentResponse commentResponse2 = new CommentResponse(
                UUID.randomUUID(), "Second comment", CommentStatus.APPROVED, postId, user.getId(), null, null);

        Page<Comment> commentPage = new PageImpl<>(List.of(comment, comment2));
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable))
                .thenReturn(commentPage);
        when(commentMapper.toResponse(comment)).thenReturn(commentResponse);
        when(commentMapper.toResponse(comment2)).thenReturn(commentResponse2);

        // Act
        PageResponse<CommentResponse> result = commentService.getAllComments(postId, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(commentResponse, commentResponse2);

        verify(commentRepository, times(1)).findByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable);
        verify(commentMapper, times(1)).toResponse(comment);
        verify(commentMapper, times(1)).toResponse(comment2);
    }
}
