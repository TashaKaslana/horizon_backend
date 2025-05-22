package org.phong.horizon.user.subdomain.role.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 255, message = "Role name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Role slug cannot be blank")
    @Size(max = 255, message = "Role slug must be less than 255 characters")
    // Consider adding a pattern validation for slug if needed
    private String slug;

    private String description;

    // Optionally, allow assigning permissions upon role creation
    private Set<UUID> permissionIds;
}

