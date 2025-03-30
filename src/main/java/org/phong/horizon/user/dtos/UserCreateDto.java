package org.phong.horizon.user.dtos;

import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserCreateDto(String auth0Id, String username, String email) implements Serializable {
}