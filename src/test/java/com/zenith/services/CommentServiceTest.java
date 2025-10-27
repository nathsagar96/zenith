package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.util.List;
import java.util.Optional;
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
    private CommentMapper commentMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentResponse commentResponse;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .content("Test Comment")
                .status(CommentStatus.APPROVED)
                .build();
        comment.setId(1L);

        commentResponse = new CommentResponse(1L, "Test Comment", "APPROVED", 1L, "johndoe", null, null);

        post = new Post();
        post.setId(1L);

        user = new User();
        user.setId(1L);
        comment.setAuthor(user);
    }

    @Test
    @DisplayName("should get all comments")
    void shouldGetAllComments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findAll(any(Pageable.class))).thenReturn(commentPage);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        PageResponse<CommentResponse> response = commentService.getAllComments(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(commentResponse, response.getContent().getFirst());

        verify(commentRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("should get all approved comments by post")
    void shouldGetAllApprovedCommentsByPost() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findApprovedByPostId(anyLong(), any(Pageable.class)))
                .thenReturn(commentPage);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        PageResponse<CommentResponse> response = commentService.getAllApprovedCommentsByPost(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(commentResponse, response.getContent().getFirst());

        verify(commentRepository, times(1)).findApprovedByPostId(1L, pageable);
    }

    @Test
    @DisplayName("should get all comments by author and status")
    void shouldGetAllCommentsByAuthorAndStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findByAuthorIdAndStatus(anyLong(), any(CommentStatus.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        PageResponse<CommentResponse> response =
                commentService.getAllCommentsByAuthorAndStatus(1L, "APPROVED", pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(commentResponse, response.getContent().getFirst());

        verify(commentRepository, times(1)).findByAuthorIdAndStatus(1L, CommentStatus.APPROVED, pageable);
    }

    @Test
    @DisplayName("should get all comments by status")
    void shouldGetAllCommentsByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findByAuthorIdAndStatus(anyLong(), any(CommentStatus.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        PageResponse<CommentResponse> response =
                commentService.getAllCommentsByStatus(CommentStatus.APPROVED, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(commentResponse, response.getContent().getFirst());

        verify(commentRepository, times(1)).findByAuthorIdAndStatus(1L, CommentStatus.APPROVED, pageable);
    }

    @Test
    @DisplayName("should get comment by id successfully")
    void shouldGetCommentByIdSuccessfully() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        CommentResponse response = commentService.getCommentById(1L);

        assertNotNull(response);
        assertEquals(commentResponse, response);

        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when comment not found by id")
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFoundById() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(1L));

        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should create comment successfully")
    void shouldCreateCommentSuccessfully() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(any(CreateCommentRequest.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        CommentResponse response = commentService.createComment(new CreateCommentRequest("Test Comment", 1L));

        assertNotNull(response);
        assertEquals(commentResponse, response);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when post not found for comment creation")
    void shouldThrowResourceNotFoundExceptionWhenPostNotFoundForCommentCreation() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(new CreateCommentRequest("Test Comment", 1L)));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("should update comment successfully")
    void shouldUpdateCommentSuccessfully() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(commentResponse);

        CommentResponse response = commentService.updateComment(1L, new UpdateCommentRequest("Updated Comment"));

        assertNotNull(response);
        assertEquals(commentResponse, response);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when comment not found for update")
    void shouldThrowResourceNotFoundExceptionWhenCommentNotFoundForUpdate() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateComment(1L, new UpdateCommentRequest("Updated Comment")));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("should approve comment successfully")
    void shouldApproveCommentSuccessfully() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentService.approveComment(1L));

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("should mark comment as spam successfully")
    void shouldMarkCommentAsSpamSuccessfully() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentService.markSpam(1L));

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("should archive comment successfully")
    void shouldArchiveCommentSuccessfully() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentService.archiveComment(1L));

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("should return true when user is comment author")
    void shouldReturnTrueWhenUserIsCommentAuthor() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);

        boolean result = commentService.isCommentAuthor(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("should return false when user is not comment author")
    void shouldReturnFalseWhenUserIsNotCommentAuthor() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(differentUser);

        boolean result = commentService.isCommentAuthor(1L);

        assertFalse(result);
    }
}
