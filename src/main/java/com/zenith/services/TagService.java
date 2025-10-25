package com.zenith.services;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.entities.Tag;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.mappers.TagMapper;
import com.zenith.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {
    private TagRepository tagRepository;
    private TagMapper tagMapper;

    public PageResponse<TagResponse> getAllTags(Pageable pageable) {
        var tags = tagRepository.findAll(pageable);
        return new PageResponse<>(
                tags.getNumber(),
                tags.getSize(),
                tags.getTotalElements(),
                tags.getTotalPages(),
                tags.stream().map(tagMapper::toResponse).toList());
    }

    public TagResponse getTagById(Long id) {
        Tag tag = findById(id);
        return tagMapper.toResponse(tag);
    }

    public TagResponse getTagByName(String name) {
        Tag tag = tagRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with name: " + name));
        return tagMapper.toResponse(tag);
    }

    @Transactional
    public TagResponse createTag(TagRequest request) {
        if (tagRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Tag with name: '" + request.name() + "' already exists");
        }
        Tag newTag = tagMapper.toEntity(request);
        Tag createdTag = tagRepository.save(newTag);
        return tagMapper.toResponse(createdTag);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagRequest request) {
        Tag existingTag = findById(id);
        if (tagRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Tag with name: '" + request.name() + "' already exists");
        }

        existingTag.setName(request.name());
        Tag updatedTag = tagRepository.save(existingTag);
        return tagMapper.toResponse(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }

    private Tag findById(Long id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
    }
}
