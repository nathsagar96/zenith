package com.zenith.repositories;

import com.zenith.BaseDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }
}
