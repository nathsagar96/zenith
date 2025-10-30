package com.zenith.repositories;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Category;
import com.zenith.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

public class PostRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User author;
    private Category category;
}
