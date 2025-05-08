package org.phong.horizon.notification.infrastructure.persistence.projections;

public interface NotificationStatisticProjection {
    String getType();
    long getTotal();
    long getUnread();
}

