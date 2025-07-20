package com.zenith.tag;

import com.zenith.tag.domain.dtos.CreateTagRequest;
import com.zenith.tag.domain.dtos.TagDto;
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
@RequestMapping("/api/v1/tags")
@Tag(name = "Tag", description = "Tag management APIs")
public class TagController {

  private final TagService tagService;
  private final TagMapper tagMapper;

  public TagController(TagService tagService, TagMapper tagMapper) {
    this.tagService = tagService;
    this.tagMapper = tagMapper;
  }

  @Operation(summary = "Get all tags")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tags")
      })
  @GetMapping
  public ResponseEntity<List<TagDto>> getAllTags() {
    return ResponseEntity.ok(tagService.getTags().stream().map(tagMapper::toTagDto).toList());
  }

  @Operation(summary = "Create new tags")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Tags created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      })
  @PostMapping
  public ResponseEntity<List<TagDto>> createTags(
      @Valid @RequestBody CreateTagRequest createTagRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            tagService.createTags(createTagRequest.names()).stream()
                .map(tagMapper::toTagDto)
                .toList());
  }

  @Operation(summary = "Delete a tag by ID")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Tag deleted successfully")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
    tagService.deleteTag(id);
    return ResponseEntity.noContent().build();
  }
}
