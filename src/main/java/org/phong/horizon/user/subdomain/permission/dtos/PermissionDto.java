package org.phong.horizon.user.subdomain.permission.dtos;

import lombok.Builder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.user.subdomain.permission.entities.Permission}
 */
public record PermissionDto(Instant createdAt, UUID id, String name, String slug, String description,
                            String module) implements Serializable {
}