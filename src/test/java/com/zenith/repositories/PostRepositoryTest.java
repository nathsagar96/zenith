package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import com.zenith.enums.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PostRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();

        author = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        userRepository.save(author);
    }

    @Test
    @DisplayName("Should find posts by status")
    void shouldFindPostsByStatus() {
        // Given
        Post post = Post.builder()
                .title("Test Post")
                .slug("test-post")
                .content("Test Content")
                .status(PostStatus.PUBLISHED)
                .author(author)
                .build();
        postRepository.save(post);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Post> result = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Test Post");
    }

    @Test
    @DisplayName("Should find posts by author ID and status")
    void shouldFindPostsByAuthorIdAndStatus() {
        // Given
        Post post = Post.builder()
                .title("Test Post")
                .slug("test-post")
                .content("Test Content")
                .status(PostStatus.PUBLISHED)
                .author(author)
                .build();
        postRepository.save(post);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Post> result = postRepository.findByAuthorIdAndStatus(author.getId(), PostStatus.PUBLISHED, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Test Post");
    }
}
