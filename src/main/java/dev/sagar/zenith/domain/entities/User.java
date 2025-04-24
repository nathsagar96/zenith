package dev.sagar.zenith.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id)
        && Objects.equals(email, user.email)
        && Objects.equals(password, user.password)
        && Objects.equals(name, user.name)
        && Objects.equals(createdAt, user.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, password, name, createdAt);
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
