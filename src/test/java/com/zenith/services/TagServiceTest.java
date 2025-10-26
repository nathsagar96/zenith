package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.entities.Tag;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.TagMapper;
import com.zenith.repositories.TagRepository;
import java.time.LocalDateTime;
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
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private TagRequest tagRequest;
    private TagResponse tagResponse;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");

        tagRequest = new TagRequest("Test Tag");
        tagResponse = new TagResponse(1L, "Test Tag", LocalDateTime.now(), LocalDateTime.now(), 1);
    }

    @Test
    @DisplayName("should get all tags")
    void shouldGetAllTags() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tagPage = new PageImpl<>(List.of(tag), pageable, 1);

        when(tagRepository.findAll(any(Pageable.class))).thenReturn(tagPage);
        when(tagMapper.toResponse(any(Tag.class))).thenReturn(tagResponse);

        PageResponse<TagResponse> response = tagService.getAllTags(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(tagResponse, response.getContent().getFirst());

        verify(tagRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("should get tag by id successfully")
    void shouldGetTagByIdSuccessfully() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(any(Tag.class))).thenReturn(tagResponse);

        TagResponse response = tagService.getTagById(1L);

        assertNotNull(response);
        assertEquals(tagResponse, response);

        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when tag not found by id")
    void shouldThrowResourceNotFoundExceptionWhenTagNotFoundById() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(1L));

        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("should get tag by name successfully")
    void shouldGetTagByNameSuccessfully() {
        when(tagRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(any(Tag.class))).thenReturn(tagResponse);

        TagResponse response = tagService.getTagByName("Test Tag");

        assertNotNull(response);
        assertEquals(tagResponse, response);

        verify(tagRepository, times(1)).findByNameIgnoreCase("Test Tag");
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when tag not found by name")
    void shouldThrowResourceNotFoundExceptionWhenTagNotFoundByName() {
        when(tagRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagByName("Test Tag"));

        verify(tagRepository, times(1)).findByNameIgnoreCase("Test Tag");
    }

    @Test
    @DisplayName("should create tag successfully")
    void shouldCreateTagSuccessfully() {
        when(tagRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(tagMapper.toEntity(any(TagRequest.class))).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toResponse(any(Tag.class))).thenReturn(tagResponse);

        TagResponse response = tagService.createTag(tagRequest);

        assertNotNull(response);
        assertEquals(tagResponse, response);

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when tag already exists")
    void shouldThrowDuplicateResourceExceptionWhenTagAlreadyExists() {
        when(tagRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> tagService.createTag(tagRequest));

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("should update tag successfully")
    void shouldUpdateTagSuccessfully() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toResponse(any(Tag.class))).thenReturn(tagResponse);

        TagResponse response = tagService.updateTag(1L, tagRequest);

        assertNotNull(response);
        assertEquals(tagResponse, response);

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when tag not found for update")
    void shouldThrowResourceNotFoundExceptionWhenTagNotFoundForUpdate() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.updateTag(1L, tagRequest));

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("should throw DuplicateResourceException when tag name already exists for update")
    void shouldThrowDuplicateResourceExceptionWhenTagNameAlreadyExistsForUpdate() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> tagService.updateTag(1L, tagRequest));

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("should delete tag successfully")
    void shouldDeleteTagSuccessfully() {
        when(tagRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> tagService.deleteTag(1L));

        verify(tagRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("should throw ResourceNotFoundException when tag not found for deletion")
    void shouldThrowResourceNotFoundExceptionWhenTagNotFoundForDeletion() {
        when(tagRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(1L));

        verify(tagRepository, never()).deleteById(1L);
    }
}
