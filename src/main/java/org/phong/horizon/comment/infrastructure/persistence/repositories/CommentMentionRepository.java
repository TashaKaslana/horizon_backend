package org.phong.horizon.comment.infrastructure.persistence.repositories;

import org.phong.horizon.comment.infrastructure.persistence.entities.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentMentionRepository extends JpaRepository<CommentMention, UUID> {
    List<CommentMention> findAllByComment_Id(UUID commentId);
}