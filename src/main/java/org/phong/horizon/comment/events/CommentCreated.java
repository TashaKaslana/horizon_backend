package org.phong.horizon.comment.events;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CommentCreated(UUID id, UUID postId, UUID userId, String content) implements Serializable {
}