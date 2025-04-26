package dev.sagar.zenith.services.impl;

import dev.sagar.zenith.domain.CreatePostRequest;
import dev.sagar.zenith.domain.PostStatus;
import dev.sagar.zenith.domain.UpdatePostRequest;
import dev.sagar.zenith.domain.entities.Category;
import dev.sagar.zenith.domain.entities.Post;
import dev.sagar.zenith.domain.entities.Tag;
import dev.sagar.zenith.domain.entities.User;
import dev.sagar.zenith.repositories.PostRepository;
import dev.sagar.zenith.services.CategoryService;
import dev.sagar.zenith.services.PostService;
import dev.sagar.zenith.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private static final int WORDS_PER_MINUTE = 200;
  private final PostRepository postRepository;
  private final CategoryService categoryService;
  private final TagService tagService;

  @Override
  @Transactional(readOnly = true)
  public List<Post> getAllPosts(UUID categoryId, UUID tagId) {

    if (categoryId != null && tagId != null) {
      Category category = categoryService.getCategoryById(categoryId);
      Tag tag = tagService.getTagById(tagId);
      return postRepository.findAllByStatusAndCategoryAndTagsContaining(
          PostStatus.PUBLISHED, category, tag);
    }

    if (categoryId != null) {
      Category category = categoryService.getCategoryById(categoryId);
      return postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, category);
    }

    if (tagId != null) {
      Tag tag = tagService.getTagById(tagId);
      return postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED, tag);
    }

    return postRepository.findAllByStatus(PostStatus.PUBLISHED);
  }

  @Override
  public List<Post> getDraftPosts(User user) {
    return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
  }

  @Override
  public Post createPost(CreatePostRequest createPostRequest, User user) {
    Post newPost =
        Post.builder()
            .title(createPostRequest.getTitle())
            .content(createPostRequest.getContent())
            .status(createPostRequest.getStatus())
            .author(user)
            .readingTime(calculateReadingTime(createPostRequest.getContent()))
            .category(categoryService.getCategoryById(createPostRequest.getCategoryId()))
            .tags(new HashSet<>(tagService.getTagsByIds(createPostRequest.getTagIds())))
            .build();

    return postRepository.save(newPost);
  }

  @Override
  @Transactional
  public Post updatePost(UUID id, UpdatePostRequest updatePostRequest) {
    Post existingPost =
        postRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

    existingPost.setTitle(updatePostRequest.getTitle());
    existingPost.setContent(updatePostRequest.getContent());
    existingPost.setStatus(updatePostRequest.getStatus());
    existingPost.setReadingTime(calculateReadingTime(updatePostRequest.getContent()));

    if (!existingPost.getCategory().getId().equals(updatePostRequest.getCategoryId())) {
      Category newCategory = categoryService.getCategoryById(updatePostRequest.getCategoryId());
      existingPost.setCategory(newCategory);
    }

    Set<UUID> existingTagIds =
        existingPost.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
    Set<UUID> newTagIds = new HashSet<>(updatePostRequest.getTagIds());
    if (!existingTagIds.equals(newTagIds)) {
      Set<Tag> newTags = new HashSet<>(tagService.getTagsByIds(updatePostRequest.getTagIds()));
      existingPost.setTags(newTags);
    }

    return postRepository.save(existingPost);
  }

  @Override
  public Post getPostById(UUID id) {
    return postRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
  }

  @Override
  public void deletePost(UUID id) {
    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    postRepository.delete(post);
  }

  private Integer calculateReadingTime(String content) {
    if (content == null || content.isEmpty()) {
      return 0;
    }

    int wordCount = content.trim().split("\\s+").length;
    return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
  }
}
