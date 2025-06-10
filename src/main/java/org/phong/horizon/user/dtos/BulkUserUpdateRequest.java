package org.phong.horizon.user.dtos;

import org.phong.horizon.user.enums.UserStatus;

import java.util.List;
import java.util.UUID;

public record BulkUserUpdateRequest(List<UUID> ids, UserStatus status, UUID roleId) {
}
