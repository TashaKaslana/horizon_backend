package org.phong.horizon.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserAccountUpdate(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email,
        @NotBlank(message = "Username cannot be blank") String username,

        @URL(message = "Profile image must be a valid URL") String profileImage,
        @URL(message = "Cover image must be a valid URL") String coverImage,
        @Size(max = 500, message = "Bio cannot exceed 500 characters") String bio
) {
}
