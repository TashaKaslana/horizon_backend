package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;

public record CreatePostCategoryRequest(@NotNull String name) {
}
