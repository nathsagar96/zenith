package dev.sagar.zenith.services.impl;

import dev.sagar.zenith.domain.entities.Category;
import dev.sagar.zenith.repositories.CategoryRepository;
import dev.sagar.zenith.services.CategoryService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

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
  public void deleteCategory(UUID id) {
    Optional<Category> category = categoryRepository.findById(id);

    if (category.isPresent() && !category.get().getPosts().isEmpty()) {
      throw new IllegalArgumentException("Category has posts associated with it");
    }

    categoryRepository.deleteById(id);
  }
}
