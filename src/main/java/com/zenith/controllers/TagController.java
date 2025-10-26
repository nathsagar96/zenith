package com.zenith.controllers;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<TagResponse> getAllTags(Pageable pageable) {
        return tagService.getAllTags(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponse getTagById(@PathVariable("id") Long id) {
        return tagService.getTagById(id);
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponse getTagByName(@PathVariable("name") String name) {
        return tagService.getTagByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponse createTag(@Valid @RequestBody TagRequest request) {
        return tagService.createTag(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponse updateTag(@PathVariable("id") Long id, @Valid @RequestBody TagRequest request) {
        return tagService.updateTag(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable("id") Long id) {
        tagService.deleteTag(id);
    }
}
