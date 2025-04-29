package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.PostReport}
 */
public record PostReportRequest(@NotNull String reason, UUID postId) implements Serializable {
  }