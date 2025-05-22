package org.phong.horizon.user.dtos;

import java.io.Serializable;
import java.util.UUID;
import org.phong.horizon.user.enums.UserStatus;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserCreatedDto(UUID id, UserStatus status) implements Serializable {
}

