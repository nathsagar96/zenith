package com.zenith.repositories;

import com.zenith.entities.Comment;
import com.zenith.enums.CommentStatus;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId AND c.status = :status")
    Page<Comment> findByAuthorIdAndStatus(
            @Param("authorId") Long authorId, @Param("status") CommentStatus status, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.status = 'APPROVED'")
    Page<Comment> findApprovedByPostId(@Param("postId") Long postId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.status = 'ARCHIVED' AND c.updatedAt < :cutoffDate")
    Long deleteArchivedCommentsOlderThan(LocalDateTime cutoffDate);

    default Long deleteArchivedCommentsOlderThan(Integer days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return deleteArchivedCommentsOlderThan(cutoffDate);
    }
}
