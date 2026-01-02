package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Tag;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TagRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository.deleteAll();
    }

    @Test
    @DisplayName("should find tag by name ignore case")
    void shouldFindTagByNameIgnoreCase() {
        // Arrange
        String tagName = "Java";
        Tag tag = Tag.builder().name(tagName).build();
        tagRepository.save(tag);

        // Act
        Optional<Tag> result = tagRepository.findByNameIgnoreCase("java");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(tagName);
    }

    @Test
    @DisplayName("should return empty when tag not found by name ignore case")
    void shouldReturnEmptyWhenTagNotFoundByNameIgnoreCase() {
        // Arrange
        String nonExistentName = "NonExistentTag";

        // Act
        Optional<Tag> result = tagRepository.findByNameIgnoreCase(nonExistentName);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return true when tag exists by name ignore case")
    void shouldReturnTrueWhenTagExistsByNameIgnoreCase() {
        // Arrange
        String tagName = "Spring";
        Tag tag = Tag.builder().name(tagName).build();
        tagRepository.save(tag);

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase("spring");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when tag does not exist by name ignore case")
    void shouldReturnFalseWhenTagDoesNotExistByNameIgnoreCase() {
        // Arrange
        String nonExistentName = "NonExistentTag";

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase(nonExistentName);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return true when tag exists with exact name")
    void shouldReturnTrueWhenTagExistsWithExactName() {
        // Arrange
        String tagName = "JavaScript";
        Tag tag = Tag.builder().name(tagName).build();
        tagRepository.save(tag);

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase(tagName);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return true when tag exists with different case")
    void shouldReturnTrueWhenTagExistsWithDifferentCase() {
        // Arrange
        String tagName = "TypeScript";
        String searchName = "typescript";
        Tag tag = Tag.builder().name(tagName).build();
        tagRepository.save(tag);

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase(searchName);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when tag does not exist")
    void shouldReturnFalseWhenTagDoesNotExist() {
        // Arrange
        String nonExistentName = "NonExistentTag";

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase(nonExistentName);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false when searching with different case for non existent tag")
    void shouldReturnFalseWhenSearchingWithDifferentCaseForNonExistentTag() {
        // Arrange
        String searchName = "nonexistenttag";

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase(searchName);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return true when multiple tags exist and one matches")
    void shouldReturnTrueWhenMultipleTagsExistAndOneMatches() {
        // Arrange
        Tag javaTag = Tag.builder().name("Java").build();
        Tag springTag = Tag.builder().name("Spring").build();
        Tag pythonTag = Tag.builder().name("Python").build();

        tagRepository.saveAll(List.of(javaTag, springTag, pythonTag));

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase("python");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when empty database")
    void shouldReturnFalseWhenEmptyDatabase() {
        // Arrange - database is empty from setup

        // Act
        boolean result = tagRepository.existsByNameIgnoreCase("AnyTag");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should save and find tag")
    void shouldSaveAndFindTag() {
        // Arrange
        Tag tag = Tag.builder().name("TestTag").build();

        // Act
        Tag savedTag = tagRepository.save(tag);
        Tag foundTag = tagRepository.findById(savedTag.getId()).orElse(null);

        // Assert
        assertThat(foundTag).isNotNull();
        assertThat(foundTag.getName()).isEqualTo("TestTag");
    }

    @Test
    @DisplayName("should delete tag by id")
    void shouldDeleteTagById() {
        // Arrange
        Tag tag = Tag.builder().name("Tag to delete").build();
        Tag savedTag = tagRepository.save(tag);

        // Act
        tagRepository.deleteById(savedTag.getId());

        // Assert
        assertThat(tagRepository.findById(savedTag.getId())).isEmpty();
    }

    @Test
    @DisplayName("should update tag name")
    void shouldUpdateTagName() {
        // Arrange
        Tag tag = Tag.builder().name("Old Tag Name").build();
        Tag savedTag = tagRepository.save(tag);

        // Act
        savedTag.setName("New Tag Name");
        Tag updatedTag = tagRepository.save(savedTag);

        // Assert
        assertThat(updatedTag.getName()).isEqualTo("New Tag Name");
    }

    @Test
    @DisplayName("should find all tags")
    void shouldFindAllTags() {
        // Arrange
        Tag tag1 = Tag.builder().name("Tag1").build();

        Tag tag2 = Tag.builder().name("Tag2").build();

        tagRepository.saveAll(List.of(tag1, tag2));

        // Act
        List<Tag> tags = tagRepository.findAll();

        // Assert
        assertThat(tags).hasSize(2);
    }

    @Test
    @DisplayName("should return empty list when no tags exist")
    void shouldReturnEmptyListWhenNoTagsExist() {
        // Act
        List<Tag> tags = tagRepository.findAll();

        // Assert
        assertThat(tags).isEmpty();
    }

    @Test
    @DisplayName("should find tag by name case insensitive")
    void shouldFindTagByNameCaseInsensitive() {
        // Arrange
        Tag tag = Tag.builder().name("CaseSensitiveTag").build();
        tagRepository.save(tag);

        // Act
        Optional<Tag> result = tagRepository.findByNameIgnoreCase("casesensitivetag");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("CaseSensitiveTag");
    }

    @Test
    @DisplayName("should not find tag with different name")
    void shouldNotFindTagWithDifferentName() {
        // Arrange
        Tag tag = Tag.builder().name("ExistingTag").build();
        tagRepository.save(tag);

        // Act
        Optional<Tag> result = tagRepository.findByNameIgnoreCase("DifferentTag");

        // Assert
        assertThat(result).isEmpty();
    }
}
