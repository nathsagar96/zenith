package com.zenith.controllers.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.security.JwtService;
import com.zenith.services.CategoryService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminCategoryController.class)
class AdminCategoryControllerTest {

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

    @BeforeEach
    void setUp() {
        categoryRequest = new CategoryRequest("Technology");
        categoryResponse = new CategoryResponse(1L, "Technology", LocalDateTime.now(), LocalDateTime.now(), 0);
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Technology"));

        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid category creation")
    void shouldReturnBadRequestForInvalidCategoryCreation() throws Exception {
        // Arrange
        CategoryRequest invalidRequest = new CategoryRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategorySuccessfully() throws Exception {
        // Arrange
        when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Technology"));

        verify(categoryService, times(1)).updateCategory(1L, categoryRequest);
    }

    @Test
    @DisplayName("Should return not found when updating non-existent category")
    void shouldReturnNotFoundWhenUpdatingNonExistentCategory() throws Exception {
        // Arrange
        when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/admin/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).updateCategory(1L, categoryRequest);
    }

    @Test
    @DisplayName("Should return bad request when updating with invalid category details")
    void shouldReturnBadRequestWhenUpdatingWithInvalidCategoryDetails() throws Exception {
        // Arrange
        CategoryRequest invalidRequest = new CategoryRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).updateCategory(anyLong(), any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategorySuccessfully() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/admin/categories/1")).andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent category")
    void shouldReturnNotFoundWhenDeletingNonExistentCategory() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService)
                .deleteCategory(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/admin/categories/1")).andExpect(status().isNotFound());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}
