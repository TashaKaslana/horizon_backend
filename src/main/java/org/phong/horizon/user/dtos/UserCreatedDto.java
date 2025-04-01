package org.phong.horizon.user.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserCreatedDto(UUID id) implements Serializable {
}