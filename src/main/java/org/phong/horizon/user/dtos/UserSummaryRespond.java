package org.phong.horizon.user.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserSummaryRespond(UUID id, String firstName, String lastName, String username, String profileImage,
                                 String coverImage) implements Serializable {
}