package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotBlank;
import org.phong.horizon.comment.enums.CommentStatus;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record UpdateCommentContentDto(@NotBlank String content, CommentStatus status) implements Serializable {
}

