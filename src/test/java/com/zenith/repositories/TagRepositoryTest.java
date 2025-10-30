package com.zenith.repositories;

import com.zenith.BaseDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class TagRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository.deleteAll();
    }
}
