package org.phong.horizon.user.dtos;

import org.phong.horizon.user.enums.UserStatus;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserIntroduction(Instant createdAt, Instant updatedAt, UUID id, String displayName, String username,
                               String email, String bio, String profileImage,
                               String coverImage, RoleDto role, UserStatus status, Instant lastLogin) implements Serializable {
}