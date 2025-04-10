package org.phong.horizon.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.infrastructure.persistence.entities.Notification;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for {@link Notification}
 */
@Getter
@AllArgsConstructor
@Builder
public final class CreateNotificationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final UUID recipientUserId;
    private final UUID postId;
    private final UUID commentId;
    private final String content;
    private final NotificationType type;
    private final Map<String, Object> extraData;
}