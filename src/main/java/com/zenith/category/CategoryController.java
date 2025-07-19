package com.zenith.category;

import com.zenith.category.domain.dtos.CategoryDto;
import com.zenith.category.domain.dtos.CreateCategoryRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
    this.categoryService = categoryService;
    this.categoryMapper = categoryMapper;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<CategoryDto> listCategories() {
    return categoryService.listCategories().stream().map(categoryMapper::toDto).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryDto createCategory(
      @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
    return categoryMapper.toDto(
        categoryService.createCategory(categoryMapper.toEntity(createCategoryRequest)));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCategory(@PathVariable UUID id) {
    categoryService.deleteCategory(id);
  }
}
