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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        var categories = categoryRepository.findAll(pageable);
        return new PageResponse<>(
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.stream().map(categoryMapper::toResponse).toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = findById(id);
        return categoryMapper.toResponse(category);
    }

    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Category with name: '" + request.name() + "' already exists");
        }

        Category newCategory = categoryMapper.toEntity(request);
        Category createdCategory = categoryRepository.save(newCategory);
        return categoryMapper.toResponse(createdCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = findById(id);

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Category with name: '" + request.name() + "' already exists");
        }

        existingCategory.setName(request.name());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Category findById(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
