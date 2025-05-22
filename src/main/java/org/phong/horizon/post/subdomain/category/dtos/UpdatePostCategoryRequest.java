package org.phong.horizon.post.subdomain.category.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;

import java.io.Serializable;

/**
 * DTO for {@link PostCategory}
 */
public record UpdatePostCategoryRequest(@NotNull @Size(max = 255) String name) implements Serializable {
  }