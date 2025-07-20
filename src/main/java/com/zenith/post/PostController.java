package com.zenith.post;

import com.zenith.auth.UserService;
import com.zenith.auth.domain.entities.User;
import com.zenith.post.domain.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Post", description = "Post management APIs")
public class PostController {

  private final PostService postService;
  private final PostMapper postMapper;
  private final UserService userService;

  public PostController(PostService postService, PostMapper postMapper, UserService userService) {
    this.postService = postService;
    this.postMapper = postMapper;
    this.userService = userService;
  }

  @Operation(summary = "Get all posts, optionally filtered by category or tag")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of published posts")
      })
  @GetMapping
  public ResponseEntity<List<PostDto>> getAllPosts(
      @RequestParam(required = false) UUID categoryId, @RequestParam(required = false) UUID tagId) {
    return ResponseEntity.ok(
        postService.getAllPosts(categoryId, tagId).stream().map(postMapper::toDto).toList());
  }

  @Operation(summary = "Get all draft posts for the authenticated user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved draft posts"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
      })
  @GetMapping("/drafts")
  public ResponseEntity<List<PostDto>> getDraftPosts(@RequestAttribute UUID userId) {
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(
        postService.getDraftPosts(user).stream().map(postMapper::toDto).toList());
  }

  @Operation(summary = "Create a new post")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Post created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Category or Tag not found")
      })
  @PostMapping
  public ResponseEntity<PostDto> createPost(
      @Valid @RequestBody CreatePostRequestDto createPostRequestDto,
      @RequestAttribute UUID userId) {
    User user = userService.getUserById(userId);
    CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(postMapper.toDto(postService.createPost(createPostRequest, user)));
  }

  @Operation(summary = "Update an existing post by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Post updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Post or Category or Tag not found")
      })
  @PutMapping("/{id}")
  public ResponseEntity<PostDto> updatePost(
      @PathVariable UUID id, @Valid @RequestBody UpdatePostRequestDto updatePostRequestDto) {
    UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
    return ResponseEntity.ok(postMapper.toDto(postService.updatePost(id, updatePostRequest)));
  }

  @Operation(summary = "Get a post by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved post"),
        @ApiResponse(responseCode = "404", description = "Post not found")
      })
  @GetMapping("/{id}")
  public ResponseEntity<PostDto> getPost(@PathVariable UUID id) {
    return ResponseEntity.ok(postMapper.toDto(postService.getPostById(id)));
  }

  @Operation(summary = "Delete a post by ID")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Post deleted successfully")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }
}
