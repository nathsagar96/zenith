package com.zenith.services;

import static org.mockito.Mockito.*;

import com.zenith.entities.Comment;
import com.zenith.entities.Post;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.PostStatus;
import com.zenith.repositories.CommentRepository;
import com.zenith.repositories.PostRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CleanupServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CleanupService cleanupService;

    @Test
    @DisplayName("Should delete archived posts and comments older than 30 days")
    void testCleanupArchivedPostsAndComments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffDate = now.minusDays(30);

        Post oldArchivedPost =
                Post.builder().title("Old Post").status(PostStatus.ARCHIVED).build();
        oldArchivedPost.setCreatedAt(cutoffDate.minusDays(1));

        Comment oldArchivedComment = Comment.builder()
                .content("Old Comment")
                .status(CommentStatus.ARCHIVED)
                .build();
        oldArchivedComment.setCreatedAt(cutoffDate.minusDays(1));

        when(postRepository.deleteArchivedPostsOlderThan(30)).thenReturn(1L);
        when(commentRepository.deleteArchivedCommentsOlderThan(30)).thenReturn(1L);

        cleanupService.cleanupArchivedPostsAndComments();

        verify(postRepository, times(1)).deleteArchivedPostsOlderThan(anyInt());
        verify(commentRepository, times(1)).deleteArchivedCommentsOlderThan(anyInt());
    }
}
