package org.phong.horizon.comment.infrastructure.persistence.repositories;

import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByPost_Id(UUID postId);

    @Modifying
    @Query("UPDATE Comment c SET c.isAuthorDeleted = true WHERE c.user.id = :id")
    void softDeleteAllByUser_Id(UUID id);

    @Modifying
    @Query("UPDATE Comment c SET c.isAuthorDeleted = false WHERE c.user.id = :id")
    void restoreAllByUser_Id(UUID id);
}