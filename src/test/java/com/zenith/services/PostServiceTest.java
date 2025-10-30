package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.responses.PostResponse;
import com.zenith.entities.Category;
import com.zenith.entities.Post;
import com.zenith.entities.Tag;
import com.zenith.entities.User;
import com.zenith.mappers.PostMapper;
import com.zenith.repositories.CategoryRepository;
import com.zenith.repositories.PostRepository;
import com.zenith.repositories.TagRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private Post post;
    private PostResponse postResponse;
    private User user;
    private Category category;
    private Tag tag;
}
