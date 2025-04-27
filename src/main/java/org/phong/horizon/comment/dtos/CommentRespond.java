package org.phong.horizon.comment.dtos;

import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CommentRespond(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id,
                             String content, UUID parentCommentId, Boolean isAuthorDeleted, UserSummaryRespond user,
                             UUID postId, Boolean isPinned) implements Serializable {
}