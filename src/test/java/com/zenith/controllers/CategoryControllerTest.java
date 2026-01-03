package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.security.JwtService;
import com.zenith.services.CategoryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;
    private PageResponse<CategoryResponse> pageResponse;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        categoryRequest = new CategoryRequest("Technology");
        categoryResponse = new CategoryResponse(categoryId, "Technology", LocalDateTime.now(), LocalDateTime.now(), 5);
        pageResponse = new PageResponse<>(0, 20, 1, 1, List.of(categoryResponse));
    }

    @Test
    @DisplayName("should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        when(categoryService.getAllCategories(any(PageRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/categories")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Technology"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters")
    void shouldReturn400ForInvalidSortParameters() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(categoryService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/categories").param("sortBy", "invalidField"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should get category by ID successfully")
    void shouldGetCategoryByIdSuccessfully() throws Exception {
        when(categoryService.getCategoryById(categoryId)).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"))
                .andExpect(jsonPath("$.categoryId").value(categoryId.toString()));
    }

    @Test
    @DisplayName("should return 404 when category not found")
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        when(categoryService.getCategoryById(categoryId))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/api/v1/categories/{categoryId}", categoryId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should create category successfully")
    void shouldCreateCategorySuccessfully() throws Exception {
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should return 409 when category name already exists")
    void shouldReturn409WhenCategoryNameAlreadyExists() throws Exception {
        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenThrow(new DuplicateResourceException("Category with name already exists"));

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should return 400 for invalid category request")
    void shouldReturn400ForInvalidCategoryRequest() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest("");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update category successfully")
    void shouldUpdateCategorySuccessfully() throws Exception {
        when(categoryService.updateCategory(eq(categoryId), any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        mockMvc.perform(put("/api/v1/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should return 404 when updating non-existent category")
    void shouldReturn404WhenUpdatingNonExistentCategory() throws Exception {
        when(categoryService.updateCategory(eq(categoryId), any(CategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(put("/api/v1/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 409 when updating to existing category name")
    void shouldReturn409WhenUpdatingToExistingCategoryName() throws Exception {
        when(categoryService.updateCategory(eq(categoryId), any(CategoryRequest.class)))
                .thenThrow(new DuplicateResourceException("Category with name already exists"));

        mockMvc.perform(put("/api/v1/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should delete category successfully")
    void shouldDeleteCategorySuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{categoryId}", categoryId)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent category")
    void shouldReturn404WhenDeletingNonExistentCategory() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService)
                .deleteCategory(categoryId);

        mockMvc.perform(delete("/api/v1/categories/{categoryId}", categoryId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 when deleting category with posts")
    void shouldReturn400WhenDeletingCategoryWithPosts() throws Exception {
        doThrow(new ValidationException("Cannot delete category wih posts"))
                .when(categoryService)
                .deleteCategory(categoryId);

        mockMvc.perform(delete("/api/v1/categories/{categoryId}", categoryId)).andExpect(status().isBadRequest());
    }
}
