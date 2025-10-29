package com.zenith.controllers;

import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.services.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@Tag(name = "Tags", description = "APIs for managing tags")
public class TagController {
    private final TagService tagService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all tags", description = "Retrieves a paginated list of all tags")
    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully")
    public PageResponse<TagResponse> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get all tags");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<TagResponse> response = tagService.getAllTags(pageable);
        log.info("Returning {} tags", response.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get tag by ID", description = "Retrieves a tag by its ID")
    @ApiResponse(responseCode = "200", description = "Tag retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    public TagResponse getTagById(@PathVariable("id") Long id) {
        log.info("Received request to get tag with id: {}", id);
        TagResponse response = tagService.getTagById(id);
        log.info("Returning tag with id: {}", id);
        return response;
    }
}
