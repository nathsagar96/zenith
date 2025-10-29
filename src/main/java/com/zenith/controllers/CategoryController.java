package com.zenith.controllers;

import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.services.CategoryService;
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
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "APIs for managing categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all categories", description = "Retrieves a paginated list of all categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public PageResponse<CategoryResponse> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("Received request to get all categories");
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<CategoryResponse> response = categoryService.getAllCategories(pageable);
        log.info("Returning {} categories", response.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get category by ID", description = "Retrieves a category by its ID")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public CategoryResponse getCategoryById(@PathVariable("id") Long id) {
        log.info("Received request to get category with id: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        log.info("Returning category with id: {}", id);
        return response;
    }
}
