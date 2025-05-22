package org.phong.horizon.admin.notification.infrastructure.specifications;

import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class AdminNotificationSpecs {

    public static Specification<AdminNotification> hasType(AdminNotificationType type) {
        return (root, _, criteriaBuilder) ->
                type == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<AdminNotification> hasSeverity(NotificationSeverity severity) {
        return (root, _, criteriaBuilder) ->
                severity == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("severity"), severity);
    }

    public static Specification<AdminNotification> isRead(Boolean isRead) {
        return (root, _, criteriaBuilder) ->
                isRead == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("isRead"), isRead);
    }

    public static Specification<AdminNotification> createdAfter(OffsetDateTime date) {
        return (root, _, criteriaBuilder) ->
                date == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<AdminNotification> createdBefore(OffsetDateTime date) {
        return (root, _, criteriaBuilder) ->
                date == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<AdminNotification> titleContains(String title) {
        return (root, _, criteriaBuilder) ->
                title == null || title.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%");
    }

    public static Specification<AdminNotification> messageContains(String message) {
        return (root, _, criteriaBuilder) ->
                message == null || message.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.like(criteriaBuilder.lower(root.get("message")),
                        "%" + message.toLowerCase() + "%");
    }
}

