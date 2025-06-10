package org.phong.horizon.user.subdomain.role.dtos;

import java.util.List;
import java.util.UUID;

public record BulkRoleDeleteRequest(List<UUID> roleIds) {
}
