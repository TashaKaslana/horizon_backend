package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.post.infrastructure.persistence.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    boolean existsByUser_IdAndPost_Id(UUID userId, UUID postId);

    Optional<Bookmark> findByUser_IdAndPost_Id(UUID id, UUID id1);

    long countAllByPost_Id(UUID postId);

    @Query("SELECT b.post.id FROM Bookmark b WHERE b.post.id IN :postIds AND b.user.id = :currentUserId")
    List<UUID> findAllByPost_IdsInAndUser_Id(List<UUID> postIds, UUID currentUserId);
}