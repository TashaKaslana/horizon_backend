package org.phong.horizon.post.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record PostCreatedDto(UUID id) implements Serializable {
}