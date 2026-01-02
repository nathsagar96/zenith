package com.zenith.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Category;
import java.util.List;
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
    @DisplayName("shouldReturnTrueWhenCategoryExistsWithExactName")
    void shouldReturnTrueWhenCategoryExistsWithExactName() {
        // Arrange
        String categoryName = "Technology";
        Category category = Category.builder().name(categoryName).build();
        categoryRepository.save(category);

        // Act
        boolean result = categoryRepository.existsByNameIgnoreCase(categoryName);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldReturnTrueWhenCategoryExistsWithDifferentCase")
    void shouldReturnTrueWhenCategoryExistsWithDifferentCase() {
        // Arrange
        String categoryName = "Technology";
        String searchName = "technology";
        Category category = Category.builder().name(categoryName).build();
        categoryRepository.save(category);

        // Act
        boolean result = categoryRepository.existsByNameIgnoreCase(searchName);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldReturnFalseWhenCategoryDoesNotExist")
    void shouldReturnFalseWhenCategoryDoesNotExist() {
        // Arrange
        String nonExistentName = "NonExistentCategory";

        // Act
        boolean result = categoryRepository.existsByNameIgnoreCase(nonExistentName);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldReturnFalseWhenSearchingWithDifferentCaseForNonExistentCategory")
    void shouldReturnFalseWhenSearchingWithDifferentCaseForNonExistentCategory() {
        // Arrange
        String searchName = "nonexistentcategory";

        // Act
        boolean result = categoryRepository.existsByNameIgnoreCase(searchName);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldReturnTrueWhenMultipleCategoriesExistAndOneMatches")
    void shouldReturnTrueWhenMultipleCategoriesExistAndOneMatches() {
        // Arrange
        Category techCategory = Category.builder().name("Technology").build();
        Category sportsCategory = Category.builder().name("Sports").build();
        Category businessCategory = Category.builder().name("Business").build();

        categoryRepository.saveAll(List.of(techCategory, sportsCategory, businessCategory));

        // Act
        boolean result = categoryRepository.existsByNameIgnoreCase("business");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldReturnFalseWhenEmptyDatabase")
    void shouldReturnFalseWhenEmptyDatabase() {
        // Arrange - database is empty from setup

        // Act
        boolean result = categoryRepository.existsByNameIgnoreCase("AnyCategory");

        // Assert
        assertThat(result).isFalse();
    }
}
