package org.phong.horizon.user.dtos;


import org.hibernate.validator.constraints.URL;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link User}
 */
public record UserUpdateDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "First name cannot be blank") String firstName,
        @NotBlank(message = "Last name cannot be blank") String lastName,
        @NotBlank(message = "Username cannot be blank") String username,

        @Size(max = 15, message = "Phone number cannot exceed 15 characters") String phoneNumber,
        @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
        @Size(max = 10, message = "Gender cannot exceed 10 characters") String gender,
        @Size(max = 500, message = "Bio cannot exceed 500 characters") String bio,

        @URL(message = "Profile image must be a valid URL") String profileImage,
        @URL(message = "Cover image must be a valid URL") String coverImage,

        @Size(max = 100, message = "Country name cannot exceed 100 characters") String country,
        @Size(max = 100, message = "City name cannot exceed 100 characters") String city
) implements Serializable {
}
