package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;
import org.phong.horizon.core.enums.InteractionType;

public record CreatePostInteraction(@NotNull InteractionType interactionType) {
}