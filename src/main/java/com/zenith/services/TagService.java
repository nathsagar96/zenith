package com.zenith.services;

import com.zenith.dtos.requests.TagRequest;
import com.zenith.dtos.responses.PageResponse;
import com.zenith.dtos.responses.TagResponse;
import com.zenith.entities.Tag;
import com.zenith.exceptions.DuplicateResourceException;
import com.zenith.exceptions.ResourceNotFoundException;
import com.zenith.exceptions.ValidationException;
import com.zenith.mappers.TagMapper;
import com.zenith.repositories.TagRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public static List<String> ALLOWED_SORT_FIELDS = List.of("name", "createdat", "updatedat");

    public void validateSortParams(String sortBy, String sortDirection) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase())) {
            throw new ValidationException("Invalid sort field: " + sortBy);
        }
        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            throw new ValidationException("Invalid sort direction: " + sortDirection);
        }
    }

    public PageResponse<TagResponse> getAllTags(Pageable pageable) {
        var tags = tagRepository.findAll(pageable);

        return new PageResponse<>(
                tags.getNumber(),
                tags.getSize(),
                tags.getTotalElements(),
                tags.getTotalPages(),
                tags.stream().map(tagMapper::toResponse).toList());
    }

    public TagResponse getTagById(UUID tagId) {
        return tagMapper.toResponse(findById(tagId));
    }

    @Transactional
    public TagResponse createTag(TagRequest request) {
        checkExistence(request.name());
        Tag newTag = tagMapper.toEntity(request);

        return tagMapper.toResponse(tagRepository.save(newTag));
    }

    @Transactional
    public TagResponse updateTag(UUID tagId, TagRequest request) {
        Tag existingTag = findById(tagId);

        checkExistence(request.name());
        existingTag.setName(request.name());

        return tagMapper.toResponse(tagRepository.save(existingTag));
    }

    @Transactional
    public void deleteTag(UUID tagId) {
        Tag tag = findById(tagId);

        if (!tag.getPosts().isEmpty()) {
            throw new ValidationException("Cannot delete tag wih posts");
        }

        tagRepository.deleteById(tagId);
    }

    private Tag findById(UUID tagId) {
        return tagRepository.findById(tagId).orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    }

    private void checkExistence(String name) {
        if (tagRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Tag with name already exists");
        }
    }
}
