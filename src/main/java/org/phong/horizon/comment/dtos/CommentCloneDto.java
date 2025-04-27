package org.phong.horizon.comment.dtos;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CommentCloneDto(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id,
                              UUID postId, UUID userId, String content, UUID parentCommentId,
                              Set<UUID> commentIds, Boolean isAuthorDeleted, Boolean isPinned) implements Serializable {
}