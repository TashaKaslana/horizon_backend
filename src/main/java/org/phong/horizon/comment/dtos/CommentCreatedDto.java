package org.phong.horizon.comment.dtos;

import java.io.Serializable;
import java.util.UUID;
import org.phong.horizon.comment.enums.CommentStatus;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CommentCreatedDto(
        UUID id,
        CommentStatus status
) implements Serializable {
}

