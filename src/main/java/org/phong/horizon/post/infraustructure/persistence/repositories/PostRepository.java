package org.phong.horizon.post.infraustructure.persistence.repositories;

import org.phong.horizon.post.infraustructure.persistence.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByUser_Id(UUID userId);

    void deleteAllByUser_Id(UUID userId);

    List<Post> findAllByVisibility(String visibility);

    List<Post> findAllByUser_IdAndVisibility(UUID userId, String visibility);
}