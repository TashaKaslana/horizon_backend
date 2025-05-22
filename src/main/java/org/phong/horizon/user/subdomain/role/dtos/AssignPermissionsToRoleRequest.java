package org.phong.horizon.user.subdomain.role.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AssignPermissionsToRoleRequest {
    @NotNull(message = "Role ID cannot be null")
    private UUID roleId;

    @NotNull(message = "Permission IDs cannot be null")
    private Set<UUID> permissionIds; // This set will represent the desired state of permissions for the role
}

