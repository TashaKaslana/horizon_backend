package org.phong.horizon.comment.infrastructure.persistence.repositories;

import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByPost_Id(UUID postId);
}