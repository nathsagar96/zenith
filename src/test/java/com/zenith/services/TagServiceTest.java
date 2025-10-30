package com.zenith.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.entities.Tag;
import com.zenith.mappers.TagMapper;
import com.zenith.repositories.TagRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private TagRequest tagRequest;
    private TagResponse tagResponse;
}
