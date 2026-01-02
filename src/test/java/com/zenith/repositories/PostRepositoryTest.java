package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Category;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import com.zenith.entities.User;
import com.zenith.enums.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    private User testUser;
    private Category testCategory;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        tagRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(com.zenith.enums.RoleType.USER)
                .build();
        userRepository.save(testUser);

        // Create test category
        testCategory = Category.builder().name("Technology").build();
        categoryRepository.save(testCategory);

        // Create test tag
        testTag = Tag.builder().name("Java").build();
        tagRepository.save(testTag);
    }

    @Test
    @DisplayName("should find posts by author id")
    void shouldFindPostsByAuthorId() {
        // Arrange
        Post post1 = Post.builder()
                .title("Post 1")
                .content("Content 1")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();

        Post post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.saveAll(List.of(post1, post2));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByAuthorId(testUser.getId(), pageable);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .allMatch(post -> post.getAuthor().getId().equals(testUser.getId()));
    }

    @Test
    @DisplayName("should return empty page when no posts found by author id")
    void shouldReturnEmptyPageWhenNoPostsFoundByAuthorId() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        UUID nonExistentUserId = UUID.randomUUID();

        // Act
        Page<Post> result = postRepository.findByAuthorId(nonExistentUserId, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find posts by author id and status")
    void shouldFindPostsByAuthorIdAndStatus() {
        // Arrange
        Post publishedPost = Post.builder()
                .title("Published Post")
                .content("Published content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();

        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.saveAll(List.of(publishedPost, draftPost));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByAuthorIdAndStatus(testUser.getId(), PostStatus.PUBLISHED, pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @DisplayName("should return empty page when no posts found by author id and status")
    void shouldReturnEmptyPageWhenNoPostsFoundByAuthorIdAndStatus() {
        // Arrange
        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.save(draftPost);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByAuthorIdAndStatus(testUser.getId(), PostStatus.PUBLISHED, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find posts by category id")
    void shouldFindPostsByCategoryId() {
        // Arrange
        Category anotherCategory = Category.builder().name("Sports").build();
        categoryRepository.save(anotherCategory);

        Post techPost = Post.builder()
                .title("Tech Post")
                .content("Tech content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();

        Post sportsPost = Post.builder()
                .title("Sports Post")
                .content("Sports content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(anotherCategory)
                .build();

        postRepository.saveAll(List.of(techPost, sportsPost));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByCategoryId(testCategory.getId(), pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getCategory().getId()).isEqualTo(testCategory.getId());
    }

    @Test
    @DisplayName("should return empty page when no posts found by category id")
    void shouldReturnEmptyPageWhenNoPostsFoundByCategoryId() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        UUID nonExistentCategoryId = UUID.randomUUID();

        // Act
        Page<Post> result = postRepository.findByCategoryId(nonExistentCategoryId, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find posts by tags name")
    void shouldFindPostsByTagsName() {
        // Arrange
        Tag anotherTag = Tag.builder().name("Spring").build();
        tagRepository.save(anotherTag);

        Post javaPost = Post.builder()
                .title("Java Post")
                .content("Java content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .tags(Set.of(testTag))
                .build();

        Post springPost = Post.builder()
                .title("Spring Post")
                .content("Spring content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .tags(Set.of(anotherTag))
                .build();

        postRepository.saveAll(List.of(javaPost, springPost));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByTagsName("Java", pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getTags()).contains(testTag);
    }

    @Test
    @DisplayName("should return empty page when no posts found by tags name")
    void shouldReturnEmptyPageWhenNoPostsFoundByTagsName() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByTagsName("NonExistentTag", pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find posts by status")
    void shouldFindPostsByStatus() {
        // Arrange
        Post publishedPost = Post.builder()
                .title("Published Post")
                .content("Published content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();

        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.saveAll(List.of(publishedPost, draftPost));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @DisplayName("should return empty page when no posts found by status")
    void shouldReturnEmptyPageWhenNoPostsFoundByStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findByStatus(PostStatus.ARCHIVED, pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find published posts")
    void shouldFindPublishedPosts() {
        // Arrange
        Post publishedPost = Post.builder()
                .title("Published Post")
                .content("Published content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();

        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.saveAll(List.of(publishedPost, draftPost));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findPublished(pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @DisplayName("should return empty page when no published posts")
    void shouldReturnEmptyPageWhenNoPublishedPosts() {
        // Arrange
        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Draft content")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.save(draftPost);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Post> result = postRepository.findPublished(pageable);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should delete archived posts older than cutoff date")
    void shouldDeleteArchivedPostsOlderThanCutoffDate() {
        // Arrange
        LocalDateTime futureCutoffDate = LocalDateTime.now().plusDays(1);

        Post archivedPost = Post.builder()
                .title("Archived Post")
                .content("Archived content")
                .status(PostStatus.ARCHIVED)
                .author(testUser)
                .category(testCategory)
                .build();
        postRepository.save(archivedPost);

        Post publishedPost = Post.builder()
                .title("Published Post")
                .content("Published content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();
        postRepository.save(publishedPost);

        // Act - this should delete the archived post since its updatedAt is in the past relative to futureCutoffDate
        Long deletedCount = postRepository.deleteArchivedPostsOlderThan(futureCutoffDate);

        // Assert
        assertThat(deletedCount).isEqualTo(1);
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(postRepository.findAll()).noneMatch(post -> post.getTitle().equals("Archived Post"));
    }

    @Test
    @DisplayName("should return zero when no archived posts to delete")
    void shouldReturnZeroWhenNoArchivedPostsToDelete() {
        // Arrange
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        Post recentArchivedPost = Post.builder()
                .title("Recent Archived Post")
                .content("Recent archived content")
                .status(PostStatus.ARCHIVED)
                .author(testUser)
                .category(testCategory)
                .build();
        recentArchivedPost.setUpdatedAt(cutoffDate.plusDays(1));
        postRepository.save(recentArchivedPost);

        // Act
        Long deletedCount = postRepository.deleteArchivedPostsOlderThan(cutoffDate);

        // Assert
        assertThat(deletedCount).isEqualTo(0);
        assertThat(postRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("should delete archived posts older than days")
    void shouldDeleteArchivedPostsOlderThanDays() {
        // Arrange
        Post archivedPost = Post.builder()
                .title("Archived Post")
                .content("Archived content")
                .status(PostStatus.ARCHIVED)
                .author(testUser)
                .category(testCategory)
                .build();
        postRepository.save(archivedPost);

        // Act - this should delete the archived post since its updatedAt is in the past relative to a future cutoff
        Long deletedCount =
                postRepository.deleteArchivedPostsOlderThan(LocalDateTime.now().plusDays(1));

        // Assert
        assertThat(deletedCount).isEqualTo(1);
        assertThat(postRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("should save and find post")
    void shouldSaveAndFindPost() {
        // Arrange
        Post post = Post.builder()
                .title("Test Post")
                .content("Test content")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .tags(Set.of(testTag))
                .build();

        // Act
        Post savedPost = postRepository.save(post);
        Post foundPost = postRepository.findById(savedPost.getId()).orElse(null);

        // Assert
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("Test Post");
        assertThat(foundPost.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(foundPost.getAuthor().getId()).isEqualTo(testUser.getId());
        assertThat(foundPost.getCategory().getId()).isEqualTo(testCategory.getId());
        assertThat(foundPost.getTags()).hasSize(1);
        assertThat(foundPost.getTags()).contains(testTag);
    }

    @Test
    @DisplayName("should delete post by id")
    void shouldDeletePostById() {
        // Arrange
        Post post = Post.builder()
                .title("Post to delete")
                .content("Content to delete")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();
        Post savedPost = postRepository.save(post);

        // Act
        postRepository.deleteById(savedPost.getId());

        // Assert
        assertThat(postRepository.findById(savedPost.getId())).isEmpty();
    }

    @Test
    @DisplayName("should update post status")
    void shouldUpdatePostStatus() {
        // Arrange
        Post post = Post.builder()
                .title("Post to update")
                .content("Content to update")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();
        Post savedPost = postRepository.save(post);

        // Act
        savedPost.setStatus(PostStatus.PUBLISHED);
        Post updatedPost = postRepository.save(savedPost);

        // Assert
        assertThat(updatedPost.getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @DisplayName("should find all posts")
    void shouldFindAllPosts() {
        // Arrange
        Post post1 = Post.builder()
                .title("Post 1")
                .content("Content 1")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .build();

        Post post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .status(PostStatus.DRAFT)
                .author(testUser)
                .category(testCategory)
                .build();

        postRepository.saveAll(List.of(post1, post2));

        // Act
        List<Post> posts = postRepository.findAll();

        // Assert
        assertThat(posts).hasSize(2);
    }

    @Test
    @DisplayName("should return empty list when no posts exist")
    void shouldReturnEmptyListWhenNoPostsExist() {
        // Act
        List<Post> posts = postRepository.findAll();

        // Assert
        assertThat(posts).isEmpty();
    }

    @Test
    @DisplayName("should handle post with tags relationship")
    void shouldHandlePostWithTagsRelationship() {
        // Arrange
        Tag tag1 = Tag.builder().name("Java").build();
        Tag tag2 = Tag.builder().name("Spring").build();
        tagRepository.saveAll(List.of(tag1, tag2));

        Post post = Post.builder()
                .title("Multi-tag Post")
                .content("Content with multiple tags")
                .status(PostStatus.PUBLISHED)
                .author(testUser)
                .category(testCategory)
                .tags(Set.of(tag1, tag2))
                .build();

        // Act
        Post savedPost = postRepository.save(post);
        Post foundPost = postRepository.findById(savedPost.getId()).orElse(null);

        // Assert
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTags()).hasSize(2);
        assertThat(foundPost.getTags()).containsExactlyInAnyOrder(tag1, tag2);
    }
}
