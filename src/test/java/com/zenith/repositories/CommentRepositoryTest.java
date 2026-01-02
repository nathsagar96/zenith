package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Category;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.CommentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class CommentRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(com.zenith.enums.RoleType.USER)
                .build();
        userRepository.save(testUser);

        // Create test category
        Category testCategory = Category.builder().name("Test Category").build();
        categoryRepository.save(testCategory);

        // Create test post
        testPost = Post.builder()
                .title("Test Post")
                .content("Test content")
                .status(com.zenith.enums.PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();
        postRepository.save(testPost);
    }

    @Test
    @DisplayName("should find comments by post id and status")
    void shouldFindCommentsByPostIdAndStatus() {
        // Arrange
        Comment approvedComment = Comment.builder()
                .content("Approved comment")
                .status(CommentStatus.APPROVED)
                .post(testPost)
                .author(testUser)
                .build();

        Comment pendingComment = Comment.builder()
                .content("Pending comment")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();

        commentRepository.saveAll(List.of(approvedComment, pendingComment));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Comment> result =
                commentRepository.findByPostIdAndStatus(testPost.getId(), CommentStatus.APPROVED, pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getContent()).isEqualTo("Approved comment");
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(CommentStatus.APPROVED);
    }

    @Test
    @DisplayName("should return empty page when no comments found by post id and status")
    void shouldReturnEmptyPageWhenNoCommentsFoundByPostIdAndStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Comment> result =
                commentRepository.findByPostIdAndStatus(testPost.getId(), CommentStatus.APPROVED, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find comments by status")
    void shouldFindCommentsByStatus() {
        // Arrange
        Comment approvedComment1 = Comment.builder()
                .content("Approved comment 1")
                .status(CommentStatus.APPROVED)
                .post(testPost)
                .author(testUser)
                .build();

        Comment approvedComment2 = Comment.builder()
                .content("Approved comment 2")
                .status(CommentStatus.APPROVED)
                .post(testPost)
                .author(testUser)
                .build();

        Comment pendingComment = Comment.builder()
                .content("Pending comment")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();

        commentRepository.saveAll(List.of(approvedComment1, approvedComment2, pendingComment));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Comment> result = commentRepository.findByStatus(CommentStatus.APPROVED, pageable);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getContent()).allMatch(comment -> comment.getStatus() == CommentStatus.APPROVED);
    }

    @Test
    @DisplayName("should return empty page when no comments found by status")
    void shouldReturnEmptyPageWhenNoCommentsFoundByStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Comment> result = commentRepository.findByStatus(CommentStatus.REJECTED, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should delete archived comments older than cutoff date")
    void shouldDeleteArchivedCommentsOlderThanCutoffDate() {
        // Arrange
        LocalDateTime futureCutoffDate = LocalDateTime.now().plusDays(1);

        Comment archivedComment = Comment.builder()
                .content("Archived comment")
                .status(CommentStatus.ARCHIVED)
                .post(testPost)
                .author(testUser)
                .build();
        commentRepository.save(archivedComment);

        Comment pendingComment = Comment.builder()
                .content("Pending comment")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();
        commentRepository.save(pendingComment);

        // Act - this should delete the archived comment since its updatedAt is in the past relative to futureCutoffDate
        Long deletedCount = commentRepository.deleteArchivedCommentsOlderThan(futureCutoffDate);

        // Assert
        assertThat(deletedCount).isEqualTo(1);
        assertThat(commentRepository.findAll()).hasSize(1);
        assertThat(commentRepository.findAll())
                .noneMatch(comment -> comment.getContent().equals("Archived comment"));
    }

    @Test
    @DisplayName("should return zero when no archived comments to delete")
    void shouldReturnZeroWhenNoArchivedCommentsToDelete() {
        // Arrange
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        Comment recentArchivedComment = Comment.builder()
                .content("Recent archived comment")
                .status(CommentStatus.ARCHIVED)
                .post(testPost)
                .author(testUser)
                .build();
        recentArchivedComment.setUpdatedAt(cutoffDate.plusDays(1));
        commentRepository.save(recentArchivedComment);

        // Act
        Long deletedCount = commentRepository.deleteArchivedCommentsOlderThan(cutoffDate);

        // Assert
        assertThat(deletedCount).isEqualTo(0);
        assertThat(commentRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("should delete archived comments older than days")
    void shouldDeleteArchivedCommentsOlderThanDays() {
        // Arrange
        Comment archivedComment = Comment.builder()
                .content("Archived comment")
                .status(CommentStatus.ARCHIVED)
                .post(testPost)
                .author(testUser)
                .build();
        commentRepository.save(archivedComment);

        // Act - this should delete the archived comment since its updatedAt is in the past relative to a future cutoff
        Long deletedCount = commentRepository.deleteArchivedCommentsOlderThan(
                LocalDateTime.now().plusDays(1));

        // Assert
        assertThat(deletedCount).isEqualTo(1);
        assertThat(commentRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("should save and find comment")
    void shouldSaveAndFindComment() {
        // Arrange
        Comment comment = Comment.builder()
                .content("Test comment")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();

        // Act
        Comment savedComment = commentRepository.save(comment);
        Comment foundComment = commentRepository.findById(savedComment.getId()).orElse(null);

        // Assert
        assertThat(foundComment).isNotNull();
        assertThat(foundComment.getContent()).isEqualTo("Test comment");
        assertThat(foundComment.getStatus()).isEqualTo(CommentStatus.PENDING);
        assertThat(foundComment.getPost().getId()).isEqualTo(testPost.getId());
        assertThat(foundComment.getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("should delete comment by id")
    void shouldDeleteCommentById() {
        // Arrange
        Comment comment = Comment.builder()
                .content("Comment to delete")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();
        Comment savedComment = commentRepository.save(comment);

        // Act
        commentRepository.deleteById(savedComment.getId());

        // Assert
        assertThat(commentRepository.findById(savedComment.getId())).isEmpty();
    }

    @Test
    @DisplayName("should update comment status")
    void shouldUpdateCommentStatus() {
        // Arrange
        Comment comment = Comment.builder()
                .content("Comment to update")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();
        Comment savedComment = commentRepository.save(comment);

        // Act
        savedComment.setStatus(CommentStatus.APPROVED);
        Comment updatedComment = commentRepository.save(savedComment);

        // Assert
        assertThat(updatedComment.getStatus()).isEqualTo(CommentStatus.APPROVED);
    }

    @Test
    @DisplayName("should find all comments")
    void shouldFindAllComments() {
        // Arrange
        Comment comment1 = Comment.builder()
                .content("Comment 1")
                .status(CommentStatus.PENDING)
                .post(testPost)
                .author(testUser)
                .build();

        Comment comment2 = Comment.builder()
                .content("Comment 2")
                .status(CommentStatus.APPROVED)
                .post(testPost)
                .author(testUser)
                .build();

        commentRepository.saveAll(List.of(comment1, comment2));

        // Act
        List<Comment> comments = commentRepository.findAll();

        // Assert
        assertThat(comments).hasSize(2);
    }

    @Test
    @DisplayName("should return empty list when no comments exist")
    void shouldReturnEmptyListWhenNoCommentsExist() {
        // Act
        List<Comment> comments = commentRepository.findAll();

        // Assert
        assertThat(comments).isEmpty();
    }
}
