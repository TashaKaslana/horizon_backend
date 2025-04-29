package org.phong.horizon.post.dtos;

import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.PostInteraction}
 */
public record PostInteractionResponse(UUID id, UserSummaryRespond user, InteractionType interaction,
                                      Instant createdAt) implements Serializable {
}