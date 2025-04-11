package org.phong.horizon.user.dtos;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.infrastructure.persistence.entities.User}
 */
public record UserCloneDto(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id,
                           String auth0Id, String firstName, String lastName, String username, String email,
                           String phoneNumber, LocalDate dateOfBirth, String gender, String bio, String profileImage,
                           String coverImage, String country, String city) implements Serializable {
}