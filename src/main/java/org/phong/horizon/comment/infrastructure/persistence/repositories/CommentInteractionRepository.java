package org.phong.horizon.comment.infrastructure.persistence.repositories;

import org.phong.horizon.infrastructure.enums.InteractionType;
import org.phong.horizon.comment.infrastructure.persistence.entities.CommentInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentInteractionRepository extends JpaRepository<CommentInteraction, UUID> {
    List<CommentInteraction> findAllByComment_Id(UUID commentId);

    boolean existsByComment_IdAndUser_IdAndInteractionType(UUID commentId, UUID userId, InteractionType interactionType);

    int deleteByComment_IdAndUser_IdAndInteractionType(UUID commentId, UUID userId, InteractionType interactionType);
}