package com.zenith.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.security.JwtService;
import com.zenith.services.TagService;
import java.util.List;
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

    private TagResponse tagResponse;
    private TagRequest tagRequest;
    private PageResponse<TagResponse> pageResponse;

    @BeforeEach
    void setUp() {
        tagResponse = new TagResponse(1L, "Technology", null, null, 0);
        tagRequest = new TagRequest("Technology");
        pageResponse = new PageResponse<>(0, 10, 1, 1, List.of(tagResponse));
    }

    @Test
    @DisplayName("should get all tags successfully")
    void shouldGetAllTagsSuccessfully() throws Exception {
        when(tagService.getAllTags(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/tags")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Technology"));
    }

    @Test
    @DisplayName("should get tag by id successfully")
    void shouldGetTagByIdSuccessfully() throws Exception {
        when(tagService.getTagById(anyLong())).thenReturn(tagResponse);

        mockMvc.perform(get("/api/v1/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should get tag by name successfully")
    void shouldGetTagByNameSuccessfully() throws Exception {
        when(tagService.getTagByName(anyString())).thenReturn(tagResponse);

        mockMvc.perform(get("/api/v1/tags/name/Technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should create tag successfully")
    void shouldCreateTagSuccessfully() throws Exception {
        when(tagService.createTag(any(TagRequest.class))).thenReturn(tagResponse);

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should update tag successfully")
    void shouldUpdateTagSuccessfully() throws Exception {
        when(tagService.updateTag(anyLong(), any(TagRequest.class))).thenReturn(tagResponse);

        mockMvc.perform(put("/api/v1/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    @DisplayName("should delete tag successfully")
    void shouldDeleteTagSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/tags/1")).andExpect(status().isNoContent());
    }
}
