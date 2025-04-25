package dev.sagar.zenith.controllers;

import dev.sagar.zenith.domain.dtos.CreateTagsRequest;
import dev.sagar.zenith.domain.dtos.TagResponse;
import dev.sagar.zenith.mappers.TagMapper;
import dev.sagar.zenith.services.TagService;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

  private final TagService tagService;
  private final TagMapper tagMapper;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<TagResponse> getAllTags() {
    return tagService.getTags().stream().map(tagMapper::toTagResponse).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public List<TagResponse> createTags(@RequestBody CreateTagsRequest createTagsRequest) {
    return tagService.createTags(createTagsRequest.getNames()).stream()
        .map(tagMapper::toTagResponse)
        .toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTag(@PathVariable UUID id) {
    tagService.deleteTag(id);
  }
}
