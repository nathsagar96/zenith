package com.zenith.services;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Category;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.CategoryMapper;
import com.zenith.repositories.CategoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public static List<String> ALLOWED_SORT_FIELDS = List.of("name", "createdat", "updatedat");

    public void validateSortParams(String sortBy, String sortDirection) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new ValidationException("Invalid sort field: " + sortBy);
        }
        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            throw new ValidationException("Invalid sort direction: " + sortDirection);
        }
    }

    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        var categories = categoryRepository.findAll(pageable);

        return new PageResponse<>(
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.stream().map(categoryMapper::toResponse).toList());
    }

    public CategoryResponse getCategoryById(UUID categoryId) {
        return categoryMapper.toResponse(findById(categoryId));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        checkExistence(request.name());
        Category newCategory = categoryMapper.toEntity(request);

        return categoryMapper.toResponse(categoryRepository.save(newCategory));
    }

    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, CategoryRequest request) {
        Category existingCategory = findById(categoryId);

        checkExistence(request.name());
        existingCategory.setName(request.name());

        return categoryMapper.toResponse(categoryRepository.save(existingCategory));
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = findById(categoryId);

        if (!category.getPosts().isEmpty()) {
            throw new ValidationException("Cannot delete category wih posts");
        }

        categoryRepository.deleteById(categoryId);
    }

    private Category findById(UUID categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private void checkExistence(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Category with name already exists");
        }
    }
}
