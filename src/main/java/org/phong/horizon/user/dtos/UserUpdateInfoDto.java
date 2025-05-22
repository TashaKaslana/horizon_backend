package org.phong.horizon.user.dtos;

import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.phong.horizon.user.enums.UserStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link User}
 */

public record UserUpdateInfoDto(
        @NotBlank(message = "First name cannot be blank") String firstName,
        @NotBlank(message = "Last name cannot be blank") String lastName,
        @NotBlank(message = "Display name cannot be blank") String displayName,

        @Size(max = 15, message = "Phone number cannot exceed 15 characters") String phoneNumber,
        @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
        @Size(max = 10, message = "Gender cannot exceed 10 characters") String gender,

        @Size(max = 100, message = "Country name cannot exceed 100 characters") String country,
        @Size(max = 100, message = "City name cannot exceed 100 characters") String city,
        @Size(max = 500, message = "Bio cannot exceed 500 characters") String bio,
        UserStatus status
) implements Serializable {
}
