package com.zenith.services;

import com.zenith.repositories.CommentRepository;
import com.zenith.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupArchivedPostsAndComments() {
        log.info("Starting cleanup of archived posts and comments older than 30 days");

        Long deletedComments = commentRepository.deleteArchivedCommentsOlderThan(30);
        log.info("Deleted {} archived comments older than 30 days", deletedComments);

        Long deletedPosts = postRepository.deleteArchivedPostsOlderThan(30);
        log.info("Deleted {} archived posts older than 30 days", deletedPosts);

        log.info("Cleanup completed successfully");
    }
}
