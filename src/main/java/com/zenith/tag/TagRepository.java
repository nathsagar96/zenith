package com.zenith.tag;

import com.zenith.tag.domain.entities.Tag;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

  @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.posts")
  List<Tag> findAllWithPostCount();

  List<Tag> findByNameIn(Set<String> names);
}
