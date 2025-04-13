package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CreateCommentDto(
        @NotNull UUID postId,
        @NotBlank String content,
        UUID parentCommentId) implements Serializable {
}