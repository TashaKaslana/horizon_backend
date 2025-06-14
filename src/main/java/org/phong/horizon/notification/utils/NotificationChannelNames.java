package org.phong.horizon.notification.utils;

import java.util.UUID;

public final class NotificationChannelNames {
    private NotificationChannelNames() {}

    /**
     * Channel for notifications targeted to a specific user
     */
    public static String notification(UUID userId) {
        return "notifications." + userId;
    }
}
