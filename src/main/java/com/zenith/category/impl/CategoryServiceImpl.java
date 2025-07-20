package com.zenith.category.impl;

import com.zenith.category.CategoryRepository;
import com.zenith.category.CategoryService;
import com.zenith.category.domain.entities.Category;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public List<Category> listCategories() {
    return categoryRepository.findAllWithPostCount();
  }

  @Override
  @Transactional
  public Category createCategory(Category category) {
    if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
      throw new IllegalArgumentException(
          "Category already exists with name: " + category.getName());
    }

    return categoryRepository.save(category);
  }

  @Override
  @Transactional
  public void deleteCategory(UUID id) {
    Optional<Category> category = categoryRepository.findById(id);

    if (category.isPresent() && !category.get().getPosts().isEmpty()) {
      throw new IllegalArgumentException("Category has posts associated with it");
    }

    categoryRepository.deleteById(id);
  }

  @Override
  public Category getCategoryById(UUID id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
  }
}
