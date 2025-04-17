package org.phong.horizon.user.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link User}
 */
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserUpdateInfoDto(
        @NotBlank(message = "First name cannot be blank") String firstName,
        @NotBlank(message = "Last name cannot be blank") String lastName,

        @Size(max = 15, message = "Phone number cannot exceed 15 characters") String phoneNumber,
        @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth,
        @Size(max = 10, message = "Gender cannot exceed 10 characters") String gender,

        @Size(max = 100, message = "Country name cannot exceed 100 characters") String country,
        @Size(max = 100, message = "City name cannot exceed 100 characters") String city
) implements Serializable {
}
