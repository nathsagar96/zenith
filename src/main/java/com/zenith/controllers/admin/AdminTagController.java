package com.zenith.controllers.admin;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.services.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tags")
public class AdminTagController {
    private final TagService tagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new tag", description = "Creates a new tag with the provided details")
    @ApiResponse(responseCode = "201", description = "Tag created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid tag details")
    public TagResponse createTag(@Valid @RequestBody TagRequest request) {
        log.info("Received request to create tag with name: {}", request.name());
        TagResponse response = tagService.createTag(request);
        log.info("Tag created successfully with id: {}", response.id());
        return response;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a tag", description = "Updates an existing tag with the provided details")
    @ApiResponse(responseCode = "200", description = "Tag updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid tag details")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    public TagResponse updateTag(@PathVariable("id") Long id, @Valid @RequestBody TagRequest request) {
        log.info("Received request to update tag with id: {}", id);
        TagResponse response = tagService.updateTag(id, request);
        log.info("Tag updated successfully with id: {}", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a tag", description = "Deletes a tag by its ID")
    @ApiResponse(responseCode = "204", description = "Tag deleted successfully")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    public void deleteTag(@PathVariable("id") Long id) {
        log.info("Received request to delete tag with id: {}", id);
        tagService.deleteTag(id);
        log.info("Tag deleted successfully with id: {}", id);
    }
}
