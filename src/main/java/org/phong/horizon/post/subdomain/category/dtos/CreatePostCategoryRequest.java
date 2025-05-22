package org.phong.horizon.post.subdomain.category.dtos;

import jakarta.validation.constraints.NotNull;

public record CreatePostCategoryRequest(@NotNull String name) {
}
