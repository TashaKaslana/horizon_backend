package org.phong.horizon.post.dtos;

import org.phong.horizon.infrastructure.enums.InteractionType;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infraustructure.persistence.entities.PostInteraction}
 */
public record PostInteractionRespond(UUID id, UserSummaryRespond user, InteractionType interaction,
                                     Instant createdAt) implements Serializable {
}