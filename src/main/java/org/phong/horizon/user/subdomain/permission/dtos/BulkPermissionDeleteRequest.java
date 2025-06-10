package org.phong.horizon.user.subdomain.permission.dtos;

import java.util.List;
import java.util.UUID;

public record BulkPermissionDeleteRequest(List<UUID> permissionIds) {
}
