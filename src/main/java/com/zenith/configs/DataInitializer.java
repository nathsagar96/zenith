package com.zenith.configs;

import com.zenith.entities.*;
import com.zenith.enums.CommentStatus;
import com.zenith.enums.PostStatus;
import com.zenith.enums.RoleType;
import com.zenith.repositories.*;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create categories
            Category techCategory = categoryRepository.save(
                    Category.builder().name("Technology").build());
            Category lifestyleCategory =
                    categoryRepository.save(Category.builder().name("Lifestyle").build());
            Category travelCategory =
                    categoryRepository.save(Category.builder().name("Travel").build());

            // Create tags
            Tag javaTag = tagRepository.save(Tag.builder().name("Java").build());
            Tag springTag = tagRepository.save(Tag.builder().name("Spring").build());
            Tag travelTag = tagRepository.save(Tag.builder().name("Travel").build());
            Tag lifestyleTag =
                    tagRepository.save(Tag.builder().name("Lifestyle").build());

            // Create admin user
            User johnDoe = User.builder()
                    .username("john_doe")
                    .email("john.doe@example.com")
                    .password(passwordEncoder.encode("SecurePass123!"))
                    .firstName("John")
                    .lastName("Doe")
                    .bio("Experienced software engineer and tech enthusiast. Admin of this platform.")
                    .role(RoleType.ADMIN)
                    .build();
            johnDoe = userRepository.save(johnDoe);

            // Create test users
            User aliceSmith = User.builder()
                    .username("alice_smith")
                    .email("alice.smith@example.com")
                    .password(passwordEncoder.encode("AlicePass456!"))
                    .firstName("Alice")
                    .lastName("Smith")
                    .bio("Passionate about technology and lifestyle. Loves to share insights and experiences.")
                    .role(RoleType.USER)
                    .build();
            aliceSmith = userRepository.save(aliceSmith);

            User bobJones = User.builder()
                    .username("bob_jones")
                    .email("bob.jones@example.com")
                    .password(passwordEncoder.encode("BobPass789!"))
                    .firstName("Bob")
                    .lastName("Jones")
                    .bio("Travel enthusiast and tech hobbyist. Enjoys writing about adventures and discoveries.")
                    .role(RoleType.USER)
                    .build();
            bobJones = userRepository.save(bobJones);

            // Create posts for admin user
            Post adminDraftPost = Post.builder()
                    .title("Spring Boot Best Practices")
                    .slug("spring-boot-best-practices")
                    .content(
                            "In this post, I'll share some of the best practices I've learned while working with Spring Boot...")
                    .status(PostStatus.DRAFT)
                    .author(johnDoe)
                    .categories(Set.of(techCategory))
                    .tags(Set.of(javaTag, springTag))
                    .build();
            adminDraftPost = postRepository.save(adminDraftPost);

            Post adminPublishedPost = Post.builder()
                    .title("Mastering Java Streams")
                    .slug("mastering-java-streams")
                    .content(
                            "Java Streams have revolutionized how we process collections in Java. Let's dive deep into their capabilities...")
                    .status(PostStatus.PUBLISHED)
                    .author(johnDoe)
                    .categories(Set.of(techCategory))
                    .tags(Set.of(javaTag))
                    .build();
            adminPublishedPost = postRepository.save(adminPublishedPost);

            Post adminArchivedPost = Post.builder()
                    .title("Legacy Java EE Patterns")
                    .slug("legacy-java-ee-patterns")
                    .content(
                            "While still useful in some contexts, many Java EE patterns have been replaced by simpler approaches...")
                    .status(PostStatus.ARCHIVED)
                    .author(johnDoe)
                    .categories(Set.of(techCategory))
                    .tags(Set.of(javaTag))
                    .build();
            adminArchivedPost = postRepository.save(adminArchivedPost);

            // Create posts for test user 1
            Post testUser1DraftPost = Post.builder()
                    .title("My Morning Routine for Productivity")
                    .slug("my-morning-routine-for-productivity")
                    .content(
                            "I've been experimenting with different morning routines to boost my productivity. Here's what works for me...")
                    .status(PostStatus.DRAFT)
                    .author(aliceSmith)
                    .categories(Set.of(lifestyleCategory))
                    .tags(Set.of(lifestyleTag))
                    .build();
            testUser1DraftPost = postRepository.save(testUser1DraftPost);

            Post testUser1PublishedPost = Post.builder()
                    .title("10 Lifestyle Hacks for Better Work-Life Balance")
                    .slug("10-lifestyle-hacks-for-better-work-life-balance")
                    .content(
                            "In today's fast-paced world, maintaining work-life balance is crucial. Here are my top 10 tips...")
                    .status(PostStatus.PUBLISHED)
                    .author(aliceSmith)
                    .categories(Set.of(lifestyleCategory))
                    .tags(Set.of(lifestyleTag))
                    .build();
            testUser1PublishedPost = postRepository.save(testUser1PublishedPost);

            Post testUser1ArchivedPost = Post.builder()
                    .title("Vintage Fashion Trends Making a Comeback")
                    .slug("vintage-fashion-trends-making-a-comeback")
                    .content(
                            "Fashion trends from the 90s are making a big comeback. Let's explore which ones are worth trying...")
                    .status(PostStatus.ARCHIVED)
                    .author(aliceSmith)
                    .categories(Set.of(lifestyleCategory))
                    .tags(Set.of(lifestyleTag))
                    .build();
            testUser1ArchivedPost = postRepository.save(testUser1ArchivedPost);

            // Create posts for test user 2
            Post testUser2DraftPost = Post.builder()
                    .title("Hidden Gems in Southeast Asia")
                    .slug("hidden-gems-in-southeast-asia")
                    .content(
                            "During my recent trip, I discovered some amazing hidden gems in Southeast Asia. Here are my favorites...")
                    .status(PostStatus.DRAFT)
                    .author(bobJones)
                    .categories(Set.of(travelCategory))
                    .tags(Set.of(travelTag))
                    .build();
            testUser2DraftPost = postRepository.save(testUser2DraftPost);

            Post testUser2PublishedPost = Post.builder()
                    .title("The Ultimate Guide to Solo Travel")
                    .slug("the-ultimate-guide-to-solo-travel")
                    .content(
                            "Solo travel can be intimidating but incredibly rewarding. Here's my comprehensive guide...")
                    .status(PostStatus.PUBLISHED)
                    .author(bobJones)
                    .categories(Set.of(travelCategory))
                    .tags(Set.of(travelTag))
                    .build();
            testUser2PublishedPost = postRepository.save(testUser2PublishedPost);

            Post testUser2ArchivedPost = Post.builder()
                    .title("Traveling on a Budget: Tips and Tricks")
                    .slug("travelling-on-a-budget-tips-and-tricks")
                    .content(
                            "You don't need to be rich to travel the world. Here are my best tips for budget travel...")
                    .status(PostStatus.ARCHIVED)
                    .author(bobJones)
                    .categories(Set.of(travelCategory))
                    .tags(Set.of(travelTag))
                    .build();
            testUser2ArchivedPost = postRepository.save(testUser2ArchivedPost);

            // Create comments for admin's published post
            Comment adminPostComment1 = Comment.builder()
                    .content(
                            "This is exactly what I needed to understand Java Streams better! Thanks for the clear explanation.")
                    .status(CommentStatus.APPROVED)
                    .post(adminPublishedPost)
                    .author(aliceSmith)
                    .build();
            commentRepository.save(adminPostComment1);

            Comment adminPostComment2 = Comment.builder()
                    .content(
                            "I've been struggling with Streams for a while. This post really helped clarify things for me.")
                    .status(CommentStatus.PENDING)
                    .post(adminPublishedPost)
                    .author(bobJones)
                    .build();
            commentRepository.save(adminPostComment2);

            // Create comments for test user 1's published post
            Comment testUser1PostComment1 = Comment.builder()
                    .content(
                            "These are some great tips! I've been struggling with work-life balance and this gives me hope.")
                    .status(CommentStatus.APPROVED)
                    .post(testUser1PublishedPost)
                    .author(johnDoe)
                    .build();
            commentRepository.save(testUser1PostComment1);

            Comment testUser1PostComment2 = Comment.builder()
                    .content("I especially liked the part about setting boundaries. Very important advice!")
                    .status(CommentStatus.PENDING)
                    .post(testUser1PublishedPost)
                    .author(bobJones)
                    .build();
            commentRepository.save(testUser1PostComment2);

            // Create comments for test user 2's published post
            Comment testUser2PostComment1 = Comment.builder()
                    .content(
                            "This guide is amazing! I've been wanting to try solo travel and this gives me the confidence to do it.")
                    .status(CommentStatus.APPROVED)
                    .post(testUser2PublishedPost)
                    .author(johnDoe)
                    .build();
            commentRepository.save(testUser2PostComment1);

            Comment testUser2PostComment2 = Comment.builder()
                    .content("I've done some solo travel and these tips are spot on. Great job!")
                    .status(CommentStatus.PENDING)
                    .post(testUser2PublishedPost)
                    .author(aliceSmith)
                    .build();
            commentRepository.save(testUser2PostComment2);

            // Create comments in different states for all posts
            createCommentsForPost(adminDraftPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(adminPublishedPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(adminArchivedPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(testUser1DraftPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(testUser1PublishedPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(testUser1ArchivedPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(testUser2DraftPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(testUser2PublishedPost, johnDoe, aliceSmith, bobJones);
            createCommentsForPost(testUser2ArchivedPost, johnDoe, aliceSmith, bobJones);
        };
    }

    private void createCommentsForPost(Post post, User adminUser, User testUser1, User testUser2) {
        // Create approved comment
        Comment approvedComment = Comment.builder()
                .content("Great insights! I learned something new from this post.")
                .status(CommentStatus.APPROVED)
                .post(post)
                .author(adminUser)
                .build();
        commentRepository.save(approvedComment);

        // Create pending comment
        Comment pendingComment = Comment.builder()
                .content("This is really helpful. I'll try to implement these ideas in my own work.")
                .status(CommentStatus.PENDING)
                .post(post)
                .author(testUser1)
                .build();
        commentRepository.save(pendingComment);

        // Create spam comment
        Comment spamComment = Comment.builder()
                .content("Check out my website for amazing deals! https://spam.com")
                .status(CommentStatus.SPAM)
                .post(post)
                .author(testUser2)
                .build();
        commentRepository.save(spamComment);

        // Create archived comment
        Comment archivedComment = Comment.builder()
                .content("I don't agree with some points here, but it's still a good read.")
                .status(CommentStatus.ARCHIVED)
                .post(post)
                .author(adminUser)
                .build();
        commentRepository.save(archivedComment);
    }
}
