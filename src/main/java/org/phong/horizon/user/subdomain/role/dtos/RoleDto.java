package org.phong.horizon.user.subdomain.role.dtos;

import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.subdomain.role.entities.Role}
 */
public record RoleDto(
        UUID id,
        String name,
        String slug,
        String description,
        Set<PermissionDto> permissions,
        Instant createdAt) {
}

