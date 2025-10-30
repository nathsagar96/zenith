package com.zenith.repositories;

import com.zenith.entities.Post;
import com.zenith.enums.PostStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByAuthorId(UUID authorId, Pageable pageable);

    Page<Post> findByAuthorIdAndStatus(UUID authorId, PostStatus status, Pageable pageable);

    Page<Post> findByCategoryId(UUID categoryId, Pageable pageable);

    Page<Post> findByTagsName(String tagName, Pageable pageable);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED'")
    Page<Post> findPublished(Pageable pageable);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.status = 'ARCHIVED' AND p.updatedAt < :cutoffDate")
    Long deleteArchivedPostsOlderThan(LocalDateTime cutoffDate);

    default Long deleteArchivedPostsOlderThan(Integer days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return deleteArchivedPostsOlderThan(cutoffDate);
    }
}
