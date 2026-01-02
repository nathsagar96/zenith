package com.zenith.controllers;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.services.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@Tag(name = "Tags", description = "Tag management operations")
public class TagController {
    private final TagService tagService;

    @Operation(
            summary = "Get all tags",
            description = "Retrieve a paginated list of all tags",
            parameters = {
                @Parameter(
                        name = "page",
                        description = "Page number (0-based index)",
                        schema = @Schema(defaultValue = "0", minimum = "0")),
                @Parameter(
                        name = "size",
                        description = "Page size",
                        schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100")),
                @Parameter(name = "sortBy", description = "Field to sort by (e.g., name, createdAt, updatedAt)"),
                @Parameter(
                        name = "sortDirection",
                        description = "Sort direction (ASC or DESC)",
                        schema = @Schema(allowableValues = {"ASC", "DESC"}))
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PageResponse.class)))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<TagResponse> getAllTags(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection) {
        tagService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return tagService.getAllTags(pageable);
    }

    @Operation(
            summary = "Get tag by ID",
            description = "Retrieve a specific tag by its ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = TagResponse.class)))
            })
    @GetMapping("/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponse getTagById(
            @Parameter(description = "ID of the tag to retrieve", required = true) @PathVariable("tagId") UUID tagId) {
        return tagService.getTagById(tagId);
    }

    @Operation(
            summary = "Create a new tag",
            description = "Create a new tag",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Tag created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = TagResponse.class)))
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TagResponse createTag(@Valid @RequestBody TagRequest request) {
        return tagService.createTag(request);
    }

    @Operation(
            summary = "Update a tag",
            description = "Update an existing tag",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Tag updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = TagResponse.class)))
            })
    @PutMapping("/{tagId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TagResponse updateTag(
            @Parameter(description = "ID of the tag to update", required = true) @PathVariable("tagId") UUID tagId,
            @Valid @RequestBody TagRequest request) {
        return tagService.updateTag(tagId, request);
    }

    @Operation(
            summary = "Delete a tag",
            description = "Delete a tag by its ID",
            responses = {@ApiResponse(responseCode = "204", description = "Tag deleted successfully")})
    @DeleteMapping("/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTag(
            @Parameter(description = "ID of the tag to delete", required = true) @PathVariable("tagId") UUID tagId) {
        tagService.deleteTag(tagId);
    }
}
