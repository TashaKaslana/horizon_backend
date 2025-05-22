package org.phong.horizon.user.subdomain.permission.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePermissionRequest {
    @Size(max = 255, message = "Permission name must be less than 255 characters")
    private String name;

    // Slug might not be updatable, or if it is, uniqueness constraints must be handled carefully.
    // For now, let's assume it's not updatable or handled by service logic.
    // @Size(max = 255, message = "Permission slug must be less than 255 characters")
    // private String slug;

    private String description;

    @Size(max = 255, message = "Permission module must be less than 255 characters")
    private String module;
}

