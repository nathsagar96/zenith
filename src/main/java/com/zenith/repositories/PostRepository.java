package com.zenith.repositories;

import com.zenith.entities.Post;
import com.zenith.enums.PostStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.status = :status")
    Page<Post> findByAuthorIdAndStatus(
            @Param("authorId") Long authorId, @Param("status") PostStatus status, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.status = 'ARCHIVED' AND p.updatedAt < :cutoffDate")
    Long deleteArchivedPostsOlderThan(LocalDateTime cutoffDate);

    default Long deleteArchivedPostsOlderThan(Integer days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return deleteArchivedPostsOlderThan(cutoffDate);
    }
}
