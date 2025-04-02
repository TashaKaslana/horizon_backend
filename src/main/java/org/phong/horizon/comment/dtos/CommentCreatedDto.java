package org.phong.horizon.comment.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CommentCreatedDto(UUID id) implements Serializable {
}