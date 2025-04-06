package org.phong.horizon.notification.dtos;

import org.phong.horizon.notification.enums.NotificationType;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.notification.infrastructure.persistence.entities.Notification}
 */
public record CreateNotificationRequest(UUID recipientUserId, UUID postId, UUID commentId,
                                        String content, NotificationType type,
                                        Map<String, Object> extraData) implements Serializable {
}