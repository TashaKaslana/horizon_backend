package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.PostReport}
 */
public record PostReportRequest(@NotNull String reason) implements Serializable {
  }