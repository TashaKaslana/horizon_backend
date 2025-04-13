package org.phong.horizon.user.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserCreateDto(
        @NotBlank(message = "Auth0 ID cannot be blank")
        @Pattern(regexp = "^[a-zA-Z0-9-]+\\|[^|]+$", message = "Auth0 ID is invalid")
        String auth0Id,

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 100, message = "Username must be in range between 3 and 100 characters")
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email
) implements Serializable {
}
