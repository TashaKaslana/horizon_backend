package org.phong.horizon.user.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserOverview(UUID id, String displayName, String username, String profileImage) implements Serializable {
}