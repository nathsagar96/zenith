package com.zenith.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Category;
import com.zenith.entities.Post;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.CategoryMapper;
import com.zenith.repositories.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryRequest categoryRequest;
    private Category category;
    private CategoryResponse categoryResponse;
    private UUID categoryId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup common test data
        categoryRequest = new CategoryRequest("Test Category");
        categoryId = UUID.randomUUID();
        category = Category.builder().name("Test Category").build();
        categoryResponse = new CategoryResponse(categoryId, "Test Category", null, null, 0);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("should validate sort params successfully when valid")
    void shouldValidateSortParamsSuccessfullyWhenValid() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            categoryService.validateSortParams("name", "asc");
            categoryService.validateSortParams("createdat", "desc");
            categoryService.validateSortParams("updatedat", "asc");
        });
    }

    @Test
    @DisplayName("should throw validation exception when sort field is invalid")
    void shouldThrowValidationExceptionWhenSortFieldIsInvalid() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> categoryService.validateSortParams("invalidField", "asc"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort field: invalidField");
    }

    @Test
    @DisplayName("should throw validation exception when sort direction is invalid")
    void shouldThrowValidationExceptionWhenSortDirectionIsInvalid() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> categoryService.validateSortParams("name", "invalidDirection"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort direction: invalidDirection");
    }

    @Test
    @DisplayName("should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() {
        // Arrange
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        PageResponse<CategoryResponse> result = categoryService.getAllCategories(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(categoryResponse);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toResponse(category);
    }

    @Test
    @DisplayName("should get category by id successfully")
    void shouldGetCategoryByIdSuccessfully() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.getCategoryById(categoryId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(categoryResponse);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toResponse(category);
    }

    @Test
    @DisplayName("should throw resource not found exception when category not found")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("should create category successfully")
    void shouldCreateCategorySuccessfully() {
        // Arrange
        when(categoryRepository.existsByNameIgnoreCase(categoryRequest.name())).thenReturn(false);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(categoryResponse);

        verify(categoryRepository, times(1)).existsByNameIgnoreCase(categoryRequest.name());
        verify(categoryMapper, times(1)).toEntity(categoryRequest);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toResponse(category);
    }

    @Test
    @DisplayName("should throw duplicate resource exception when creating category with duplicate name")
    void shouldThrowDuplicateResourceExceptionWhenCreatingCategoryWithDuplicateName() {
        // Arrange
        when(categoryRepository.existsByNameIgnoreCase(categoryRequest.name())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(categoryRequest));

        verify(categoryRepository, times(1)).existsByNameIgnoreCase(categoryRequest.name());
        verify(categoryMapper, never()).toEntity(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        // Arrange
        CategoryRequest updateRequest = new CategoryRequest("Updated Category");
        Category updatedCategory = Category.builder().name("Updated Category").build();
        CategoryResponse updatedResponse = new CategoryResponse(categoryId, "Updated Category", null, null, 0);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase(updateRequest.name())).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toResponse(updatedCategory)).thenReturn(updatedResponse);

        // Act
        CategoryResponse result = categoryService.updateCategory(categoryId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedResponse);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).existsByNameIgnoreCase(updateRequest.name());
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toResponse(updatedCategory);
    }

    @Test
    @DisplayName("should throw resource not found exception when updating non existent category")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentCategory() {
        // Arrange
        CategoryRequest updateRequest = new CategoryRequest("Updated Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryId, updateRequest));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).existsByNameIgnoreCase(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw duplicate resource exception when updating category with duplicate name")
    void shouldThrowDuplicateResourceExceptionWhenUpdatingCategoryWithDuplicateName() {
        // Arrange
        CategoryRequest updateRequest = new CategoryRequest("Duplicate Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase(updateRequest.name())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> categoryService.updateCategory(categoryId, updateRequest));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).existsByNameIgnoreCase(updateRequest.name());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("should delete category successfully")
    void shouldDeleteCategorySuccessfully() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act & Assert
        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("should throw resource not found exception when deleting non existent category")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentCategory() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("should throw validation exception when deleting category with posts")
    void shouldThrowValidationExceptionWhenDeletingCategoryWithPosts() {
        // Arrange
        Category categoryWithPosts = Category.builder().name("Test Category").build();
        categoryWithPosts.getPosts().add(mock(Post.class)); // Non-empty list to trigger validation

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryWithPosts));

        // Act & Assert
        ValidationException exception =
                assertThrows(ValidationException.class, () -> categoryService.deleteCategory(categoryId));

        assertThat(exception.getMessage()).isEqualTo("Cannot delete category wih posts");

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }
}
