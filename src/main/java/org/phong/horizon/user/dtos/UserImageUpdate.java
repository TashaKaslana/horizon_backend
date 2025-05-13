package org.phong.horizon.user.dtos;

import org.hibernate.validator.constraints.URL;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserImageUpdate(@URL(message = "Profile image must be a valid URL") String profileImage,
                              @URL(message = "Cover image must be a valid URL") String coverImage) {

}
