package dev.sagar.zenith.controllers;

import dev.sagar.zenith.domain.dtos.CategoryDto;
import dev.sagar.zenith.domain.dtos.CreateCategoryRequest;
import dev.sagar.zenith.mappers.CategoryMapper;
import dev.sagar.zenith.services.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

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
