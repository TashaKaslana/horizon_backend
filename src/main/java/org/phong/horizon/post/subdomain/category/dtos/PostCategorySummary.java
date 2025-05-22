package org.phong.horizon.post.subdomain.category.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link PostCategory}
 */
public record PostCategorySummary(Instant createdAt, UUID id, @NotNull @Size(max = 255) String name) implements Serializable {
  }