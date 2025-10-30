package com.zenith.controllers;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.services.CategoryService;
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management operations")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Retrieve a paginated list of all categories",
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
    public PageResponse<CategoryResponse> getAllCategories(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection) {
        categoryService.validateSortParams(sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return categoryService.getAllCategories(pageable);
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieve a specific category by its ID",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful retrieval",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CategoryResponse.class)))
            })
    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getCategoryById(
            @Parameter(description = "ID of the category to retrieve", required = true) @PathVariable("categoryId")
                    UUID categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @Operation(
            summary = "Create a new category",
            description = "Create a new category (admin only)",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Category created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CategoryResponse.class)))
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @Operation(
            summary = "Update a category",
            description = "Update an existing category (admin only)",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Category updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CategoryResponse.class)))
            })
    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(
            @Parameter(description = "ID of the category to update", required = true) @PathVariable("categoryId")
                    UUID categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(categoryId, request);
    }

    @Operation(
            summary = "Delete a category",
            description = "Delete a category by its ID (admin only)",
            responses = {@ApiResponse(responseCode = "204", description = "Category deleted successfully")})
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true) @PathVariable("categoryId")
                    UUID categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
