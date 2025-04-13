package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record UpdateCommentContentDto(@NotBlank String content) implements Serializable {
}