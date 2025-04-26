package dev.sagar.zenith.services;

import dev.sagar.zenith.domain.CreatePostRequest;
import dev.sagar.zenith.domain.UpdatePostRequest;
import dev.sagar.zenith.domain.entities.Post;
import dev.sagar.zenith.domain.entities.User;
import java.util.List;
import java.util.UUID;

public interface PostService {

  List<Post> getAllPosts(UUID categoryId, UUID tagId);

  List<Post> getDraftPosts(User user);

  Post createPost(CreatePostRequest createPostRequest, User user);

  Post updatePost(UUID id, UpdatePostRequest updatePostRequest);

  Post getPostById(UUID id);

  void deletePost(UUID id);
}
