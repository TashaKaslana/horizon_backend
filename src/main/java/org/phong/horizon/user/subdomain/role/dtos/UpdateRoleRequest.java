package org.phong.horizon.user.subdomain.role.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @Size(max = 255, message = "Role name must be less than 255 characters")
    private String name;

    // Slug is typically not updatable to avoid breaking references.
    // If it needs to be updatable, ensure uniqueness and impact are handled.
    // private String slug;

    private String description;
}

