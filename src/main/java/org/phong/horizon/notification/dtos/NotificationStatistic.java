package org.phong.horizon.notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.phong.horizon.notification.enums.NotificationType;

import java.util.Map;

@Getter
@AllArgsConstructor
public class NotificationStatistic {

    private final int allCount;
    private final int allUnreadCount;
    private final Map<NotificationType, NotificationCount> stats;

    public NotificationCount get(NotificationType type) {
        return stats.getOrDefault(type, new NotificationCount(0, 0));
    }

        public record NotificationCount(int count, int unreadCount) {
    }

    public static NotificationStatistic from(Map<NotificationType, NotificationCount> stats) {
        int total = 0;
        int totalUnread = 0;

        for (NotificationCount count : stats.values()) {
            total += count.count();
            totalUnread += count.unreadCount();
        }

        return new NotificationStatistic(total, totalUnread, stats);
    }
}
