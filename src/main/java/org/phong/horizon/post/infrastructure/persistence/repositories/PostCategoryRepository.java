package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.post.infrastructure.persistence.entities.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostCategoryRepository extends JpaRepository<PostCategory, UUID> {
    PostCategory getReferenceByName(String name);

    boolean existsByName(String name);

    Optional<PostCategory> findByName(String name);
}