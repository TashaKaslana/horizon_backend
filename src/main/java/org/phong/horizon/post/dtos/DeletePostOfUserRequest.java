package org.phong.horizon.post.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infraustructure.persistence.entities.Post}
 */
public record DeletePostOfUserRequest(UUID userId) implements Serializable {
}