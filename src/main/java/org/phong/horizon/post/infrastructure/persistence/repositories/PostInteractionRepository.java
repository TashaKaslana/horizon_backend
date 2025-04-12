package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.post.infrastructure.persistence.entities.PostInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, UUID> {
    boolean existsByUser_IdAndPost_IdAndInteraction(UUID userId, UUID postId, InteractionType interaction);

    Optional<PostInteraction> findByPost_IdAndUser_IdAndInteraction(UUID postId, UUID userId, InteractionType interaction);

    List<PostInteraction> findAllByPost_Id(UUID postId);

    void deleteAllByUser_Id(UUID userId);

    long countAllByPost_Id(UUID postId);
}