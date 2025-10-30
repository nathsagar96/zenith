package com.zenith.repositories;

import com.zenith.BaseDataJpaTest;
import com.zenith.entities.Post;
import com.zenith.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    private User author;
    private Post post;
}
