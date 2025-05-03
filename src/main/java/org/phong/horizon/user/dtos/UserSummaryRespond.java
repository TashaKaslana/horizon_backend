package org.phong.horizon.user.dtos;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserSummaryRespond(UUID id, String displayName, String username, String profileImage,
                                 String coverImage, Instant createdAt) implements Serializable {
}