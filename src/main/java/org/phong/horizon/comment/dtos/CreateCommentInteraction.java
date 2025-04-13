package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotNull;
import org.phong.horizon.core.enums.InteractionType;

public record CreateCommentInteraction(@NotNull InteractionType interactionType) {
}
