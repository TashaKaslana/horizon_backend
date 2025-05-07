package org.phong.horizon.notification.utils;

import org.phong.horizon.notification.enums.NotificationType;
import static org.phong.horizon.notification.enums.NotificationType.*;

public class NotificationTypeHelper {
    public static String getGroupType(String type) {
        return switch (type) {
            case "LIKE_POST", "LIKE_COMMENT" -> "like";
            case "NEW_FOLLOWER", "UN_FOLLOWER" -> "follow";
            case "REPLY_COMMENT", "COMMENT_PINNED", "REPORT_COMMENT" -> "comment";
            case "MENTION_COMMENT" -> "mention";
            case "COMMENT_POST", "REPORT_POST" -> "post";
            case "SYSTEM_MESSAGE" -> "system";
            default -> type;
        };
    }

    public static NotificationType[] convertGroupType(String groupType) {
        return switch (groupType) {
            case "like" -> new NotificationType[]{LIKE_POST, LIKE_COMMENT};
            case "follow" -> new NotificationType[]{NEW_FOLLOWER, UN_FOLLOWER};
            case "comment" -> new NotificationType[]{REPLY_COMMENT, COMMENT_PINNED, REPORT_COMMENT};
            case "mention" -> new NotificationType[]{MENTION_COMMENT};
            case "post" -> new NotificationType[]{COMMENT_POST, REPORT_POST};
            case "system" -> new NotificationType[]{SYSTEM_MESSAGE};
            default -> new NotificationType[]{NotificationType.valueOf(groupType)};
        };
    }
}
