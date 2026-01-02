package com.zenith.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.TagMapper;
import com.zenith.repositories.TagRepository;
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
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    private TagRequest tagRequest;
    private Tag tag;
    private TagResponse tagResponse;
    private UUID tagId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup common test data
        tagRequest = new TagRequest("Test Tag");
        tagId = UUID.randomUUID();
        tag = Tag.builder().name("Test Tag").build();
        tagResponse = new TagResponse(tagId, "Test Tag", null, null, 0);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("shouldValidateSortParamsSuccessfullyWhenValid")
    void shouldValidateSortParamsSuccessfullyWhenValid() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            tagService.validateSortParams("name", "asc");
            tagService.validateSortParams("createdat", "desc");
            tagService.validateSortParams("updatedat", "asc");
        });
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenSortFieldIsInvalid")
    void shouldThrowValidationExceptionWhenSortFieldIsInvalid() {
        // Act & Assert
        ValidationException exception =
                assertThrows(ValidationException.class, () -> tagService.validateSortParams("invalidField", "asc"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort field: invalidField");
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenSortDirectionIsInvalid")
    void shouldThrowValidationExceptionWhenSortDirectionIsInvalid() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> tagService.validateSortParams("name", "invalidDirection"));

        assertThat(exception.getMessage()).isEqualTo("Invalid sort direction: invalidDirection");
    }

    @Test
    @DisplayName("shouldGetAllTagsSuccessfully")
    void shouldGetAllTagsSuccessfully() {
        // Arrange
        Page<Tag> tagPage = new PageImpl<>(List.of(tag));
        when(tagRepository.findAll(pageable)).thenReturn(tagPage);
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // Act
        PageResponse<TagResponse> result = tagService.getAllTags(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(tagResponse);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(tagRepository, times(1)).findAll(pageable);
        verify(tagMapper, times(1)).toResponse(tag);
    }

    @Test
    @DisplayName("shouldGetTagByIdSuccessfully")
    void shouldGetTagByIdSuccessfully() {
        // Arrange
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // Act
        TagResponse result = tagService.getTagById(tagId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(tagResponse);

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagMapper, times(1)).toResponse(tag);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenTagNotFound")
    void shouldThrowResourceNotFoundExceptionWhenTagNotFound() {
        // Arrange
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(tagId));

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("shouldCreateTagSuccessfully")
    void shouldCreateTagSuccessfully() {
        // Arrange
        when(tagRepository.existsByNameIgnoreCase(tagRequest.name())).thenReturn(false);
        when(tagMapper.toEntity(tagRequest)).thenReturn(tag);
        when(tagRepository.save(tag)).thenReturn(tag);
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // Act
        TagResponse result = tagService.createTag(tagRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(tagResponse);

        verify(tagRepository, times(1)).existsByNameIgnoreCase(tagRequest.name());
        verify(tagMapper, times(1)).toEntity(tagRequest);
        verify(tagRepository, times(1)).save(tag);
        verify(tagMapper, times(1)).toResponse(tag);
    }

    @Test
    @DisplayName("shouldThrowDuplicateResourceExceptionWhenCreatingTagWithDuplicateName")
    void shouldThrowDuplicateResourceExceptionWhenCreatingTagWithDuplicateName() {
        // Arrange
        when(tagRepository.existsByNameIgnoreCase(tagRequest.name())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> tagService.createTag(tagRequest));

        verify(tagRepository, times(1)).existsByNameIgnoreCase(tagRequest.name());
        verify(tagMapper, never()).toEntity(any());
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldUpdateTagSuccessfully")
    void shouldUpdateTagSuccessfully() {
        // Arrange
        TagRequest updateRequest = new TagRequest("Updated Tag");
        Tag updatedTag = Tag.builder().name("Updated Tag").build();
        TagResponse updatedResponse = new TagResponse(tagId, "Updated Tag", null, null, 0);

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameIgnoreCase(updateRequest.name())).thenReturn(false);
        when(tagRepository.save(tag)).thenReturn(updatedTag);
        when(tagMapper.toResponse(updatedTag)).thenReturn(updatedResponse);

        // Act
        TagResponse result = tagService.updateTag(tagId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedResponse);

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).existsByNameIgnoreCase(updateRequest.name());
        verify(tagRepository, times(1)).save(tag);
        verify(tagMapper, times(1)).toResponse(updatedTag);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentTag")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentTag() {
        // Arrange
        TagRequest updateRequest = new TagRequest("Updated Tag");
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.updateTag(tagId, updateRequest));

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, never()).existsByNameIgnoreCase(any());
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldThrowDuplicateResourceExceptionWhenUpdatingTagWithDuplicateName")
    void shouldThrowDuplicateResourceExceptionWhenUpdatingTagWithDuplicateName() {
        // Arrange
        TagRequest updateRequest = new TagRequest("Duplicate Tag");
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameIgnoreCase(updateRequest.name())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> tagService.updateTag(tagId, updateRequest));

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).existsByNameIgnoreCase(updateRequest.name());
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldDeleteTagSuccessfully")
    void shouldDeleteTagSuccessfully() {
        // Arrange
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        // Act & Assert
        assertDoesNotThrow(() -> tagService.deleteTag(tagId));

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentTag")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentTag() {
        // Arrange
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(tagId));

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("shouldThrowValidationExceptionWhenDeletingTagWithPosts")
    void shouldThrowValidationExceptionWhenDeletingTagWithPosts() {
        // Arrange
        Tag tagWithPosts = Tag.builder()
                .name("Test Tag")
                .posts(List.of(mock(Post.class))) // Non-empty list to trigger validation
                .build();

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tagWithPosts));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> tagService.deleteTag(tagId));

        assertThat(exception.getMessage()).isEqualTo("Cannot delete tag wih posts");

        verify(tagRepository, times(1)).findById(tagId);
        verify(tagRepository, never()).deleteById(any());
    }
}
