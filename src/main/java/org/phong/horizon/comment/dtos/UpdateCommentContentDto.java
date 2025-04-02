package org.phong.horizon.comment.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record UpdateCommentContentDto(String content) implements Serializable {
  }