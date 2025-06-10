package org.phong.horizon.notification.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "NotificationType", description = "Type of notification event")
public enum NotificationType {
    LIKE_POST,
    NEW_FOLLOWER,
    UN_FOLLOWER,
    COMMENT_POST,
    LIKE_COMMENT,
    MENTION_COMMENT,
    REPLY_COMMENT,
    REPORT_COMMENT,
    REPORT_POST,
    COMMENT_PINNED,
    SYSTEM_MESSAGE
}
