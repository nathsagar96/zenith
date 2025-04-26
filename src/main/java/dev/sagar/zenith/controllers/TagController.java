package dev.sagar.zenith.controllers;

import dev.sagar.zenith.domain.dtos.CreateTagRequest;
import dev.sagar.zenith.domain.dtos.TagDto;
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
  public List<TagDto> getAllTags() {
    return tagService.getTags().stream().map(tagMapper::toTagDto).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public List<TagDto> createTags(@RequestBody CreateTagRequest createTagRequest) {
    return tagService.createTags(createTagRequest.getNames()).stream()
        .map(tagMapper::toTagDto)
        .toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTag(@PathVariable UUID id) {
    tagService.deleteTag(id);
  }
}
