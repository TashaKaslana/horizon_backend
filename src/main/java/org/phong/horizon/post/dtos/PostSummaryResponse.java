package org.phong.horizon.post.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record PostSummaryResponse(UUID id, UUID createdBy, UUID userId, String caption,
                                  String description) implements Serializable {
}