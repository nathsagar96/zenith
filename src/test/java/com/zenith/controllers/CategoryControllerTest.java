package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.CategoryRequest;
import com.zenith.dtos.responses.CategoryResponse;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.security.JwtService;
import com.zenith.services.CategoryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
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

    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;
    private PageResponse<CategoryResponse> pageResponse;

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse(1L, "Technology", null, null, 0);
        categoryRequest = new CategoryRequest("Technology");
        pageResponse = new PageResponse<>(0, 10, 1, 1, List.of(categoryResponse));
    }

    @Test
    @DisplayName("should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        when(categoryService.getAllCategories(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Technology"));
    }

    @Test
    @DisplayName("should get category by id successfully")
    void shouldGetCategoryByIdSuccessfully() throws Exception {
        when(categoryService.getCategoryById(anyLong())).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"));
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
    @DisplayName("should update category successfully")
    void shouldUpdateCategorySuccessfully() throws Exception {
        when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should delete category successfully")
    void shouldDeleteCategorySuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/1")).andExpect(status().isNoContent());
    }
}
