package com.zenith.tag.impl;

import com.zenith.tag.TagRepository;
import com.zenith.tag.TagService;
import com.zenith.tag.domain.entities.Tag;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  public TagServiceImpl(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Override
  public List<Tag> getTags() {
    return tagRepository.findAllWithPostCount();
  }

  @Override
  @Transactional
  public List<Tag> createTags(Set<String> tagNames) {
    List<Tag> existingTags = tagRepository.findByNameIn(tagNames);

    Set<String> existingTagNames =
        existingTags.stream().map(Tag::getName).collect(Collectors.toSet());

    List<Tag> newTags =
        tagNames.stream()
            .filter(tagName -> !existingTagNames.contains(tagName))
            .map(Tag::new)
            .toList();

    List<Tag> savedTags = new ArrayList<>();
    if (!newTags.isEmpty()) {
      savedTags = tagRepository.saveAll(newTags);
    }

    savedTags.addAll(existingTags);
    return savedTags;
  }

  @Override
  @Transactional
  public void deleteTag(UUID id) {
    tagRepository
        .findById(id)
        .ifPresent(
            tag -> {
              if (!tag.getPosts().isEmpty()) {
                throw new IllegalStateException("Cannot delete tag with associated posts");
              }
              tagRepository.deleteById(id);
            });
  }

  @Override
  public Tag getTagById(UUID id) {
    return tagRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
  }

  @Override
  public List<Tag> getTagsByIds(Set<UUID> ids) {
    List<Tag> tags = tagRepository.findAllById(ids);
    if (tags.size() != ids.size()) {
      throw new EntityNotFoundException("Some tags not found with the provided ids");
    }
    return tags;
  }
}
