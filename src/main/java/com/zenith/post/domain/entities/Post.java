package com.zenith.post.domain.entities;

import com.zenith.auth.domain.entities.User;
import com.zenith.category.domain.entities.Category;
import com.zenith.tag.domain.entities.Tag;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "posts")
public class Post {

  @ManyToMany
  @JoinTable(
      name = "post_tags",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private final Set<Tag> tags = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PostStatus status;

  @Column(nullable = false)
  private Integer readingTime;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Post() {}

  public Post(
      String title,
      String content,
      PostStatus status,
      User user,
      Integer integer,
      Category categoryById,
      List<Tag> tags) {
    this.title = title;
    this.content = content;
    this.status = status;
    this.author = user;
    this.readingTime = integer;
    this.category = categoryById;
    this.tags.addAll(tags);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Post post = (Post) o;
    return Objects.equals(id, post.id)
        && Objects.equals(title, post.title)
        && Objects.equals(content, post.content)
        && status == post.status
        && Objects.equals(readingTime, post.readingTime)
        && Objects.equals(createdAt, post.createdAt)
        && Objects.equals(updatedAt, post.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, status, readingTime, createdAt, updatedAt);
  }

  @PrePersist
  private void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  private void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags.addAll(tags);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public PostStatus getStatus() {
    return status;
  }

  public void setStatus(PostStatus status) {
    this.status = status;
  }

  public Integer getReadingTime() {
    return readingTime;
  }

  public void setReadingTime(Integer readingTime) {
    this.readingTime = readingTime;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
