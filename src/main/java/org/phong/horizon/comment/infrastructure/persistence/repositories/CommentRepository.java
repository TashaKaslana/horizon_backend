package org.phong.horizon.comment.infrastructure.persistence.repositories;

import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findAllByPost_Id(Pageable pageable, UUID postId);

    @Modifying
    @Query("UPDATE Comment c SET c.isAuthorDeleted = true WHERE c.user.id = :id")
    void softDeleteAllByUser_Id(UUID id);

    @Modifying
    @Query("UPDATE Comment c SET c.isAuthorDeleted = false WHERE c.user.id = :id")
    void restoreAllByUser_Id(UUID id);

    long countAllByPost_Id(UUID postId);

    @Modifying
    @Query("UPDATE Comment c SET c.isPinned = true WHERE c.post.id = :postId")
    void removePinnedCommentInPost(UUID postId);

    @Query("SELECT c.post.id, COUNT(c.id) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countCommentsByIds(List<UUID> postIds);
}