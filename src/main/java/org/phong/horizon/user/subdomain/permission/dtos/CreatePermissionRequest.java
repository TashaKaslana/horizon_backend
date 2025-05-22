package org.phong.horizon.user.subdomain.permission.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePermissionRequest {
    @NotBlank(message = "Permission name cannot be blank")
    @Size(max = 255, message = "Permission name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Permission slug cannot be blank")
    @Size(max = 255, message = "Permission slug must be less than 255 characters")
    // Consider adding a pattern validation for slug if needed
    private String slug;

    private String description;

    @NotBlank(message = "Permission module cannot be blank")
    @Size(max = 255, message = "Permission module must be less than 255 characters")
    private String module;
}

