package dev.sagar.zenith.repositories;

import dev.sagar.zenith.domain.entities.Post;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {}
