package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Tag;
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
    @DisplayName("Should find tag by name ignoring case")
    void shouldFindTagByNameIgnoringCase() {
        // Given
        Tag tag = Tag.builder().name("Test Tag").build();
        tagRepository.save(tag);

        // When
        Optional<Tag> found = tagRepository.findByNameIgnoreCase("test tag");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Tag");
    }

    @Test
    @DisplayName("Should return true when tag exists by name ignoring case")
    void shouldReturnTrueWhenTagExistsByNameIgnoringCase() {
        // Given
        Tag tag = Tag.builder().name("Test Tag").build();
        tagRepository.save(tag);

        // When
        boolean exists = tagRepository.existsByNameIgnoreCase("test tag");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when tag does not exist by name ignoring case")
    void shouldReturnFalseWhenTagDoesNotExistByNameIgnoringCase() {
        // When
        boolean exists = tagRepository.existsByNameIgnoreCase("Non Existent");

        // Then
        assertThat(exists).isFalse();
    }
}
