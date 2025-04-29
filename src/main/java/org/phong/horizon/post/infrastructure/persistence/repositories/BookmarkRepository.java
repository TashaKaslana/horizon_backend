package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.post.infrastructure.persistence.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    boolean existsByUser_IdAndPost_Id(UUID userId, UUID postId);

    Optional<Bookmark> findByUser_IdAndPost_Id(UUID id, UUID id1);
}