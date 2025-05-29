package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.post.infrastructure.persistence.entities.PostInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, UUID> {
    boolean existsByUser_IdAndPost_IdAndInteraction(UUID userId, UUID postId, InteractionType interaction);

    Optional<PostInteraction> findByPost_IdAndUser_IdAndInteraction(UUID postId, UUID userId, InteractionType interaction);

    List<PostInteraction> findAllByPost_Id(UUID postId);

    void deleteAllByUser_Id(UUID userId);

    long countAllByPost_Id(UUID postId);

    boolean existsByPost_IdAndUser_IdAndInteraction(UUID postId, UUID currentUserId, InteractionType interactionType);

    @Query("SELECT pi.post.id FROM PostInteraction pi WHERE pi.post.id IN :postIds AND pi.user.id = :currentUserId")
    List<UUID> findAllByPost_IdsInAndUser_Id(List<UUID> postIds, UUID currentUserId);

    @Query("SELECT pi.post.id, COUNT(pi) FROM PostInteraction pi WHERE pi.post.id IN :postIds GROUP BY pi.post.id")
    List<Object[]> countPostInteractionByPostIds(List<UUID> postIds);

    long countByCreatedAtAfter(Instant createdAtAfter);
}