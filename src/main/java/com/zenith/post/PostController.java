package com.zenith.post;

import com.zenith.auth.UserService;
import com.zenith.auth.domain.entities.User;
import com.zenith.post.domain.dtos.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

  private final PostService postService;
  private final PostMapper postMapper;
  private final UserService userService;

  public PostController(PostService postService, PostMapper postMapper, UserService userService) {
    this.postService = postService;
    this.postMapper = postMapper;
    this.userService = userService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<PostDto> getAllPosts(
      @RequestParam(required = false) UUID categoryId, @RequestParam(required = false) UUID tagId) {
    return postService.getAllPosts(categoryId, tagId).stream().map(postMapper::toDto).toList();
  }

  @GetMapping("/drafts")
  @ResponseStatus(HttpStatus.OK)
  public List<PostDto> getDraftPosts(@RequestAttribute UUID id) {
    User user = userService.getUserById(id);
    return postService.getDraftPosts(user).stream().map(postMapper::toDto).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PostDto createPost(
      @Valid @RequestBody CreatePostRequestDto createPostRequestDto, @RequestAttribute UUID id) {
    User user = userService.getUserById(id);
    CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
    return postMapper.toDto(postService.createPost(createPostRequest, user));
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public PostDto updatePost(
      @PathVariable UUID id, @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto) {
    UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
    return postMapper.toDto(postService.updatePost(id, updatePostRequest));
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public PostDto getPost(@PathVariable UUID id) {
    return postMapper.toDto(postService.getPostById(id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePost(@PathVariable UUID id) {
    postService.deletePost(id);
  }
}
