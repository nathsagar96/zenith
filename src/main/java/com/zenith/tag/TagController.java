package com.zenith.tag;

import com.zenith.tag.domain.dtos.CreateTagRequest;
import com.zenith.tag.domain.dtos.TagDto;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

  private final TagService tagService;
  private final TagMapper tagMapper;

  public TagController(TagService tagService, TagMapper tagMapper) {
    this.tagService = tagService;
    this.tagMapper = tagMapper;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<TagDto> getAllTags() {
    return tagService.getTags().stream().map(tagMapper::toTagDto).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public List<TagDto> createTags(@RequestBody CreateTagRequest createTagRequest) {
    return tagService.createTags(createTagRequest.names()).stream()
        .map(tagMapper::toTagDto)
        .toList();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTag(@PathVariable UUID id) {
    tagService.deleteTag(id);
  }
}
