package org.phong.horizon.user.dtos;

import org.phong.horizon.user.enums.UserStatus;
import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for {@link User}
 */
public record UserRespondDto(Instant createdAt, Instant updatedAt, UUID id, String auth0Id, String firstName,
                             String lastName, String username, String email, String phoneNumber, LocalDate dateOfBirth,
                             String gender, String bio, String profileImage, String coverImage, String country,
                             String city, Instant deletedAt, String displayName,
                             UserStatus status, RoleDto role, Instant lastLogin) implements Serializable {
}

