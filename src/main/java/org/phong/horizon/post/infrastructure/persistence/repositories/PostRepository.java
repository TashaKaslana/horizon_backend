package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByUser_Id(UUID userId);

    void deleteAllByUser_Id(UUID userId);

    List<Post> findAllByVisibility(Visibility visibility);

    List<Post> findAllByUser_IdAndVisibility(UUID userId, Visibility visibility);
}