package com.zenith.repositories;

import com.zenith.entities.User;
import com.zenith.enums.RoleType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByRole(RoleType role, Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
