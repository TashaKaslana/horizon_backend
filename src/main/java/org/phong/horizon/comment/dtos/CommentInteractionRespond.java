package org.phong.horizon.comment.dtos;

import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.CommentInteraction}
 */
public record CommentInteractionRespond(UUID id, UserSummaryRespond user, String interactionType,
                                        Instant createdAt) implements Serializable {
}