package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.PostCategory}
 */
public record PostCategorySummary(Instant createdAt, UUID id, @NotNull @Size(max = 255) String name) implements Serializable {
  }