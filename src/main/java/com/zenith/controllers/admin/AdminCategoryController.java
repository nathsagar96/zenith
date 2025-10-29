package com.zenith.controllers.admin;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.services.CategoryService;
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
@RequestMapping("/api/v1/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category", description = "Creates a new category with the provided details")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid category details")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Received request to create category with name: {}", request.name());
        CategoryResponse response = categoryService.createCategory(request);
        log.info("Category created successfully with id: {}", response.id());
        return response;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a category", description = "Updates an existing category with the provided details")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid category details")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public CategoryResponse updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryRequest request) {
        log.info("Received request to update category with id: {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        log.info("Category updated successfully with id: {}", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category", description = "Deletes a category by its ID")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public void deleteCategory(@PathVariable("id") Long id) {
        log.info("Received request to delete category with id: {}", id);
        categoryService.deleteCategory(id);
        log.info("Category deleted successfully with id: {}", id);
    }
}
