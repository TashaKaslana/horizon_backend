package org.phong.horizon.notification.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
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
@Builder
@ToString
public final class CreateNotificationRequest implements Serializable {
    @Serial
    @JsonIgnore
    private static final long serialVersionUID = 0L;

    @NotNull
    private final UUID recipientUserId;

    private final UUID postId;

    private final UUID commentId;

    @NotBlank
    private final String content;

    @NotNull
    private final NotificationType type;

    private final Map<String, Object> extraData;

    public CreateNotificationRequest(UUID recipientUserId, UUID postId, UUID commentId, String content, NotificationType type, Map<String, Object> extraData) {
        this.recipientUserId = recipientUserId;
        this.postId = postId;
        this.commentId = commentId;
        this.content = content;
        this.type = type;
        this.extraData = extraData;
    }

    public CreateNotificationRequest(UUID recipientUserId, String content, NotificationType type) {
        this(recipientUserId, null, null, content, type, null);
    }

    public CreateNotificationRequest(UUID recipientUserId, String content, NotificationType type, Map<String, Object> extraData) {
        this(recipientUserId, null, null, content, type, extraData);
    }
}