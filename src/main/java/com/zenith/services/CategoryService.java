package com.zenith.services;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Category;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.CategoryMapper;
import com.zenith.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories");
        var categories = categoryRepository.findAll(pageable);
        return new PageResponse<>(
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.stream().map(categoryMapper::toResponse).toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);
        Category category = findById(id);
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse getCategoryByName(String name) {
        log.info("Fetching category with name: {}", name);
        Category category = categoryRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating category with name: {}", request.name());

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Category creation failed: Category with name '{}' already exists", request.name());
            throw new DuplicateResourceException("Category with name: '" + request.name() + "' already exists");
        }

        Category newCategory = categoryMapper.toEntity(request);
        Category createdCategory = categoryRepository.save(newCategory);
        log.info("Category created successfully with id: {}", createdCategory.getId());
        return categoryMapper.toResponse(createdCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category existingCategory = findById(id);

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Category update failed: Category with name '{}' already exists", request.name());
            throw new DuplicateResourceException("Category with name: '" + request.name() + "' already exists");
        }

        existingCategory.setName(request.name());

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());
        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        if (!categoryRepository.existsById(id)) {
            log.warn("Category deletion failed: Category not found with id: {}", id);
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with id: {}", id);
    }

    private Category findById(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
