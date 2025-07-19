package com.zenith.post;

import com.zenith.auth.domain.entities.User;
import com.zenith.post.domain.dtos.CreatePostRequest;
import com.zenith.post.domain.dtos.UpdatePostRequest;
import com.zenith.post.domain.entities.Post;
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
