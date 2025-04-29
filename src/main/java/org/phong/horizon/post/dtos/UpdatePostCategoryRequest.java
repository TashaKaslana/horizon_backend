package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.PostCategory}
 */
public record UpdatePostCategoryRequest(@NotNull @Size(max = 255) String name) implements Serializable {
  }