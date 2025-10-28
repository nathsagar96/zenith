package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.entities.Category;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.CategoryMapper;
import com.zenith.repositories.CategoryRepository;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private Page<CategoryResponse> page;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("Test Category").build();
        category.setId(1L);

        categoryRequest = new CategoryRequest("Test Category");
        categoryResponse = new CategoryResponse(1L, "Test Category", null, null, 0);
    }

    @Test
    @DisplayName("should get all categories")
    void shouldGetAllCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(categoryResponse);

        PageResponse<CategoryResponse> response = categoryService.getAllCategories(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(categoryResponse, response.getContent().getFirst());

        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("should get category by id successfully")
    void shouldGetCategoryByIdSuccessfully() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(categoryResponse);

        CategoryResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(categoryResponse, response);

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when category not found by id")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundById() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should create category successfully")
    void shouldCreateCategorySuccessfully() {
        when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(categoryMapper.toEntity(any(CategoryRequest.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(categoryResponse);

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        assertNotNull(response);
        assertEquals(categoryResponse, response);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when category already exists")
    void shouldThrowDuplicateResourceExceptionWhenCategoryAlreadyExists() {
        when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(categoryRequest));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("should update category successfully")
    void shouldUpdateCategorySuccessfully() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(categoryResponse);

        CategoryResponse response = categoryService.updateCategory(1L, categoryRequest);

        assertNotNull(response);
        assertEquals(categoryResponse, response);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when category not found for update")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundForUpdate() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1L, categoryRequest));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when category name already exists for update")
    void shouldThrowDuplicateResourceExceptionWhenCategoryNameAlreadyExistsForUpdate() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.updateCategory(1L, categoryRequest));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("should delete category successfully")
    void shouldDeleteCategorySuccessfully() {
        when(categoryRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when category not found for deletion")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundForDeletion() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));

        verify(categoryRepository, never()).deleteById(1L);
    }
}
