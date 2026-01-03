package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.security.JwtService;
import com.zenith.services.TagService;
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
@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TagControllerTest {

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
    private PageResponse<TagResponse> pageResponse;
    private UUID tagId;

    @BeforeEach
    void setUp() {
        tagId = UUID.randomUUID();
        tagRequest = new TagRequest("Spring Boot");
        tagResponse = new TagResponse(tagId, "Spring Boot", LocalDateTime.now(), LocalDateTime.now(), 8);
        pageResponse = new PageResponse<>(0, 20, 1, 1, List.of(tagResponse));
    }

    @Test
    @DisplayName("should get all tags successfully")
    void shouldGetAllTagsSuccessfully() throws Exception {
        when(tagService.getAllTags(any(PageRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/tags")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Spring Boot"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("should return 400 for invalid sort parameters")
    void shouldReturn400ForInvalidSortParameters() throws Exception {
        doThrow(new ValidationException("Invalid sort field: invalidField"))
                .when(tagService)
                .validateSortParams(anyString(), anyString());

        mockMvc.perform(get("/api/v1/tags").param("sortBy", "invalidField")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should get tag by ID successfully")
    void shouldGetTagByIdSuccessfully() throws Exception {
        when(tagService.getTagById(tagId)).thenReturn(tagResponse);

        mockMvc.perform(get("/api/v1/tags/{tagId}", tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Boot"))
                .andExpect(jsonPath("$.tagId").value(tagId.toString()));
    }

    @Test
    @DisplayName("should return 404 when tag not found")
    void shouldReturn404WhenTagNotFound() throws Exception {
        when(tagService.getTagById(tagId)).thenThrow(new ResourceNotFoundException("Tag not found"));

        mockMvc.perform(get("/api/v1/tags/{tagId}", tagId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should create tag successfully")
    void shouldCreateTagSuccessfully() throws Exception {
        when(tagService.createTag(any(TagRequest.class))).thenReturn(tagResponse);

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    @DisplayName("should return 409 when tag name already exists")
    void shouldReturn409WhenTagNameAlreadyExists() throws Exception {
        when(tagService.createTag(any(TagRequest.class)))
                .thenThrow(new DuplicateResourceException("Tag with name already exists"));

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should return 400 for invalid tag request")
    void shouldReturn400ForInvalidTagRequest() throws Exception {
        TagRequest invalidRequest = new TagRequest("");

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should update tag successfully")
    void shouldUpdateTagSuccessfully() throws Exception {
        when(tagService.updateTag(eq(tagId), any(TagRequest.class))).thenReturn(tagResponse);

        mockMvc.perform(put("/api/v1/tags/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    @DisplayName("should return 404 when updating non-existent tag")
    void shouldReturn404WhenUpdatingNonExistentTag() throws Exception {
        when(tagService.updateTag(eq(tagId), any(TagRequest.class)))
                .thenThrow(new ResourceNotFoundException("Tag not found"));

        mockMvc.perform(put("/api/v1/tags/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 409 when updating to existing tag name")
    void shouldReturn409WhenUpdatingToExistingTagName() throws Exception {
        when(tagService.updateTag(eq(tagId), any(TagRequest.class)))
                .thenThrow(new DuplicateResourceException("Tag with name already exists"));

        mockMvc.perform(put("/api/v1/tags/{tagId}", tagId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("should delete tag successfully")
    void shouldDeleteTagSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/tags/{tagId}", tagId)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent tag")
    void shouldReturn404WhenDeletingNonExistentTag() throws Exception {
        doThrow(new ResourceNotFoundException("Tag not found")).when(tagService).deleteTag(tagId);

        mockMvc.perform(delete("/api/v1/tags/{tagId}", tagId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 400 when deleting tag with posts")
    void shouldReturn400WhenDeletingTagWithPosts() throws Exception {
        doThrow(new ValidationException("Cannot delete tag wih posts"))
                .when(tagService)
                .deleteTag(tagId);

        mockMvc.perform(delete("/api/v1/tags/{tagId}", tagId)).andExpect(status().isBadRequest());
    }
}
