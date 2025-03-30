package org.phong.horizon.user.dtos;

import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link User}
 */
public record UserUpdateDto(String firstName, String lastName, String username, String phoneNumber,
                            LocalDate dateOfBirth, String gender, String bio, String profileImage, String coverImage,
                            String country, String city) implements Serializable {
}