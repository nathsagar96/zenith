package com.zenith.category;

import com.zenith.category.domain.dtos.CategoryDto;
import com.zenith.category.domain.dtos.CreateCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
    this.categoryService = categoryService;
    this.categoryMapper = categoryMapper;
  }

  @Operation(summary = "List all categories")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of categories")
      })
  @GetMapping
  public ResponseEntity<List<CategoryDto>> listCategories() {
    return ResponseEntity.ok(
        categoryService.listCategories().stream().map(categoryMapper::toDto).toList());
  }

  @Operation(summary = "Create a new category")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
      })
  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(
      @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            categoryMapper.toDto(
                categoryService.createCategory(categoryMapper.toEntity(createCategoryRequest))));
  }

  @Operation(summary = "Delete a category by ID")
  @ApiResponses(
      value = {@ApiResponse(responseCode = "204", description = "Category deleted successfully")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}
