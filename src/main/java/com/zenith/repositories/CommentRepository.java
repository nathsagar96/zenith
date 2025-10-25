package com.zenith.repositories;

import com.zenith.entities.Comment;
import com.zenith.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId AND c.status = :status")
    Page<Comment> findByAuthorIdAndStatus(
            @Param("authorId") Long authorId, @Param("status") CommentStatus status, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.status = 'APPROVED'")
    Page<Comment> findApprovedByPostId(@Param("postId") Long postId, Pageable pageable);
}
