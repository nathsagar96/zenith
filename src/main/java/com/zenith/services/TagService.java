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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public PageResponse<TagResponse> getAllTags(Pageable pageable) {
        log.info("Fetching all tags");
        var tags = tagRepository.findAll(pageable);
        return new PageResponse<>(
                tags.getNumber(),
                tags.getSize(),
                tags.getTotalElements(),
                tags.getTotalPages(),
                tags.stream().map(tagMapper::toResponse).toList());
    }

    public TagResponse getTagById(Long id) {
        log.info("Fetching tag with id: {}", id);
        Tag tag = findById(id);
        return tagMapper.toResponse(tag);
    }

    @Transactional
    public TagResponse createTag(TagRequest request) {
        log.info("Creating tag with name: {}", request.name());

        if (tagRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Tag creation failed: Tag with name '{}' already exists", request.name());
            throw new DuplicateResourceException("Tag with name: '" + request.name() + "' already exists");
        }

        Tag newTag = tagMapper.toEntity(request);
        Tag createdTag = tagRepository.save(newTag);
        log.info("Tag created successfully with id: {}", createdTag.getId());
        return tagMapper.toResponse(createdTag);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagRequest request) {
        log.info("Updating tag with id: {}", id);

        Tag existingTag = findById(id);
        if (tagRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Tag update failed: Tag with name '{}' already exists", request.name());
            throw new DuplicateResourceException("Tag with name: '" + request.name() + "' already exists");
        }

        existingTag.setName(request.name());
        Tag updatedTag = tagRepository.save(existingTag);
        log.info("Tag updated successfully with id: {}", updatedTag.getId());
        return tagMapper.toResponse(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {
        log.info("Deleting tag with id: {}", id);

        if (!tagRepository.existsById(id)) {
            log.warn("Tag deletion failed: Tag not found with id: {}", id);
            throw new ResourceNotFoundException("Tag not found with id: " + id);
        }

        tagRepository.deleteById(id);
        log.info("Tag deleted successfully with id: {}", id);
    }

    private Tag findById(Long id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
    }
}
