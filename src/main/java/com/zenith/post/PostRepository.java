package com.zenith.post;

import com.zenith.auth.domain.entities.User;
import com.zenith.category.domain.entities.Category;
import com.zenith.post.domain.entities.Post;
import com.zenith.post.domain.entities.PostStatus;
import com.zenith.tag.domain.entities.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
  List<Post> findAllByStatusAndCategoryAndTagsContaining(
      PostStatus status, Category category, Tag tag);

  List<Post> findAllByStatusAndCategory(PostStatus status, Category category);

  List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);

  List<Post> findAllByStatus(PostStatus status);

  List<Post> findAllByAuthorAndStatus(User author, PostStatus status);
}
