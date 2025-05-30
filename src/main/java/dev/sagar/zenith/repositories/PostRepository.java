package dev.sagar.zenith.repositories;

import dev.sagar.zenith.domain.PostStatus;
import dev.sagar.zenith.domain.entities.Category;
import dev.sagar.zenith.domain.entities.Post;
import dev.sagar.zenith.domain.entities.Tag;
import dev.sagar.zenith.domain.entities.User;
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
