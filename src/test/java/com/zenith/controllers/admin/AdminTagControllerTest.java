package com.zenith.controllers.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.security.JwtService;
import com.zenith.services.TagService;
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
@WebMvcTest(AdminTagController.class)
class AdminTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private TagRequest tagRequest;
    private TagResponse tagResponse;

    @BeforeEach
    void setUp() {
        tagRequest = new TagRequest("Java");
        tagResponse = new TagResponse(1L, "Java", LocalDateTime.now(), LocalDateTime.now(), 0);
    }

    @Test
    @DisplayName("Should create tag successfully")
    void shouldCreateTagSuccessfully() throws Exception {
        // Arrange
        when(tagService.createTag(any(TagRequest.class))).thenReturn(tagResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Java"));

        verify(tagService, times(1)).createTag(any(TagRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid tag creation")
    void shouldReturnBadRequestForInvalidTagCreation() throws Exception {
        // Arrange
        TagRequest invalidRequest = new TagRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tagService, never()).createTag(any(TagRequest.class));
    }

    @Test
    @DisplayName("Should update tag successfully")
    void shouldUpdateTagSuccessfully() throws Exception {
        // Arrange
        when(tagService.updateTag(anyLong(), any(TagRequest.class))).thenReturn(tagResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/admin/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Java"));

        verify(tagService, times(1)).updateTag(1L, tagRequest);
    }

    @Test
    @DisplayName("Should return not found when updating non-existent tag")
    void shouldReturnNotFoundWhenUpdatingNonExistentTag() throws Exception {
        // Arrange
        when(tagService.updateTag(anyLong(), any(TagRequest.class)))
                .thenThrow(new ResourceNotFoundException("Tag not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/admin/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isNotFound());

        verify(tagService, times(1)).updateTag(1L, tagRequest);
    }

    @Test
    @DisplayName("Should return bad request when updating with invalid tag details")
    void shouldReturnBadRequestWhenUpdatingWithInvalidTagDetails() throws Exception {
        // Arrange
        TagRequest invalidRequest = new TagRequest("");

        // Act & Assert
        mockMvc.perform(put("/api/v1/admin/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tagService, never()).updateTag(anyLong(), any(TagRequest.class));
    }

    @Test
    @DisplayName("Should delete tag successfully")
    void shouldDeleteTagSuccessfully() throws Exception {
        // Arrange
        doNothing().when(tagService).deleteTag(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/admin/tags/1")).andExpect(status().isNoContent());

        verify(tagService, times(1)).deleteTag(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent tag")
    void shouldReturnNotFoundWhenDeletingNonExistentTag() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Tag not found")).when(tagService).deleteTag(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/admin/tags/1")).andExpect(status().isNotFound());

        verify(tagService, times(1)).deleteTag(1L);
    }
}
