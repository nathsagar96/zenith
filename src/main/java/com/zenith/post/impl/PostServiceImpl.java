package com.zenith.post.impl;

import com.zenith.auth.domain.entities.User;
import com.zenith.category.CategoryService;
import com.zenith.category.domain.entities.Category;
import com.zenith.post.PostRepository;
import com.zenith.post.PostService;
import com.zenith.post.domain.dtos.CreatePostRequest;
import com.zenith.post.domain.dtos.UpdatePostRequest;
import com.zenith.post.domain.entities.Post;
import com.zenith.post.domain.entities.PostStatus;
import com.zenith.tag.TagService;
import com.zenith.tag.domain.entities.Tag;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

  private static final int WORDS_PER_MINUTE = 200;
  private final PostRepository postRepository;
  private final CategoryService categoryService;
  private final TagService tagService;

  public PostServiceImpl(
      PostRepository postRepository, CategoryService categoryService, TagService tagService) {
    this.postRepository = postRepository;
    this.categoryService = categoryService;
    this.tagService = tagService;
  }

  @Override
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
  @Transactional
  public Post createPost(CreatePostRequest createPostRequest, User user) {
    Post newPost =
        new Post(
            createPostRequest.title(),
            createPostRequest.content(),
            createPostRequest.status(),
            user,
            calculateReadingTime(createPostRequest.content()),
            categoryService.getCategoryById(createPostRequest.categoryId()),
            tagService.getTagsByIds(createPostRequest.tagIds()));

    return postRepository.save(newPost);
  }

  @Override
  @Transactional
  public Post updatePost(UUID id, UpdatePostRequest updatePostRequest) {
    Post existingPost =
        postRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

    existingPost.setTitle(updatePostRequest.title());
    existingPost.setContent(updatePostRequest.content());
    existingPost.setStatus(updatePostRequest.status());
    existingPost.setReadingTime(calculateReadingTime(updatePostRequest.content()));

    if (!existingPost.getCategory().getId().equals(updatePostRequest.categoryId())) {
      Category newCategory = categoryService.getCategoryById(updatePostRequest.categoryId());
      existingPost.setCategory(newCategory);
    }

    Set<UUID> existingTagIds =
        existingPost.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
    Set<UUID> newTagIds = new HashSet<>(updatePostRequest.tagIds());
    if (!existingTagIds.equals(newTagIds)) {
      Set<Tag> newTags = new HashSet<>(tagService.getTagsByIds(updatePostRequest.tagIds()));
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
  @Transactional
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
