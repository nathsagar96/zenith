package com.zenith.repositories;

import com.zenith.BaseDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
}
