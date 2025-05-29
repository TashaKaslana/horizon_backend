package org.phong.horizon.post.dtos;

import java.io.Serializable;
import java.util.UUID;
import org.phong.horizon.post.enums.PostStatus;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record PostSummaryResponse(UUID id, UUID createdBy, UUID userId, String caption,
                                  String description, PostStatus status, String videoThumbnailUrl) implements Serializable {
}

