package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.CommentStatus;
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
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User author;
    private Post post;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();

        author = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(author);

        post = Post.builder()
                .title("Test Post")
                .slug("test-post")
                .content("Test Content")
                .author(author)
                .build();
        postRepository.save(post);
    }

    @Test
    @DisplayName("Should find comments by author ID and status")
    void shouldFindCommentsByAuthorIdAndStatus() {
        // Given
        Comment comment = Comment.builder()
                .content("Test Comment")
                .author(author)
                .post(post)
                .status(CommentStatus.APPROVED)
                .build();
        commentRepository.save(comment);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Comment> result =
                commentRepository.findByAuthorIdAndStatus(author.getId(), CommentStatus.APPROVED, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getContent()).isEqualTo("Test Comment");
    }

    @Test
    @DisplayName("Should find approved comments by post ID")
    void shouldFindApprovedCommentsByPostId() {
        // Given
        Comment comment = Comment.builder()
                .content("Test Comment")
                .author(author)
                .post(post)
                .status(CommentStatus.APPROVED)
                .build();
        commentRepository.save(comment);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Comment> result = commentRepository.findApprovedByPostId(post.getId(), pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getContent()).isEqualTo("Test Comment");
    }
}
