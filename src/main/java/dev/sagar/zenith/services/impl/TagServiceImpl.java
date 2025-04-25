package dev.sagar.zenith.services.impl;

import dev.sagar.zenith.domain.entities.Tag;
import dev.sagar.zenith.repositories.TagRepository;
import dev.sagar.zenith.services.TagService;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

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
            .map(name -> Tag.builder().name(name).posts(new HashSet<>()).build())
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
}
