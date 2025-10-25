package com.zenith.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "categories")
    private List<Post> posts = new ArrayList<>();
}
