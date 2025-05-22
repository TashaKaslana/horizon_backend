package org.phong.horizon.user.subdomain.role.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private Set<PermissionDto> permissions;
}

