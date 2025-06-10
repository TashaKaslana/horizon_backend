package org.phong.horizon.admin.notification.infrastructure.dtos;

import java.util.List;
import java.util.UUID;

public record BulkAdminNotificationUpdateRequest(List<UUID> notificationIds, Boolean isRead) {
}
