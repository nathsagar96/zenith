package com.zenith.repositories;

import com.zenith.entities.Comment;
import com.zenith.enums.CommentStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByPostIdAndStatus(UUID postId, CommentStatus status, Pageable pageable);

    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.status = 'ARCHIVED' AND c.updatedAt < :cutoffDate")
    Long deleteArchivedCommentsOlderThan(LocalDateTime cutoffDate);

    default Long deleteArchivedCommentsOlderThan(Integer days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return deleteArchivedCommentsOlderThan(cutoffDate);
    }
}
