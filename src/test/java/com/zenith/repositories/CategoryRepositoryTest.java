package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Category;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find category by name ignoring case")
    void shouldFindCategoryByNameIgnoringCase() {
        // Given
        Category category = Category.builder().name("Test Category").build();
        categoryRepository.save(category);

        // When
        Optional<Category> found = categoryRepository.findByNameIgnoreCase("test category");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Category");
    }

    @Test
    @DisplayName("Should return true when category exists by name ignoring case")
    void shouldReturnTrueWhenCategoryExistsByNameIgnoringCase() {
        // Given
        Category category = Category.builder().name("Test Category").build();
        categoryRepository.save(category);

        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("test category");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when category does not exist by name ignoring case")
    void shouldReturnFalseWhenCategoryDoesNotExistByNameIgnoringCase() {
        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("Non Existent");

        // Then
        assertThat(exists).isFalse();
    }
}
