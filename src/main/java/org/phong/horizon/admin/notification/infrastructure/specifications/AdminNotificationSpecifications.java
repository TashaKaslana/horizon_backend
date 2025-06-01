package org.phong.horizon.admin.notification.infrastructure.specifications;

import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;
import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class AdminNotificationSpecifications {
    public static Specification<AdminNotification> hasType(AdminNotificationType type) {
        return (root, _, cb) -> type == null ?
            cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<AdminNotification> hasSeverity(NotificationSeverity severity) {
        return (root, _, cb) -> severity == null ?
            cb.conjunction() : cb.equal(root.get("severity"), severity);
    }

    public static Specification<AdminNotification> createdAfter(OffsetDateTime date) {
        return (root, _, cb) -> date == null ?
            cb.conjunction() : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<AdminNotification> createdBefore(OffsetDateTime date) {
        return (root, _, cb) -> date == null ?
            cb.conjunction() : cb.lessThan(root.get("createdAt"), date);
    }

    public static Specification<AdminNotification> createdBetween(OffsetDateTime start, OffsetDateTime end) {
        return Specification
            .where(createdAfter(start))
            .and(createdBefore(end));
    }

    public static Specification<AdminNotification> isRead(Boolean read) {
        return (root, _, cb) -> read == null ?
            cb.conjunction() : cb.equal(root.get("isRead"), read);
    }
}
