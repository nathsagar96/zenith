package com.zenith.entities;

import com.zenith.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "comments",
        indexes = {
            @Index(name = "idx_comment_post_id", columnList = "post_id"),
            @Index(name = "idx_comment_author_id", columnList = "author_id"),
            @Index(name = "idx_comment_status", columnList = "status")
        })
public class Comment extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status = CommentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
