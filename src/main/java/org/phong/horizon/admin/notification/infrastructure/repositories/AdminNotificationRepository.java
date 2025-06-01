package org.phong.horizon.admin.notification.infrastructure.repositories;

import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;
import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.phong.horizon.admin.notification.infrastructure.specifications.AdminNotificationSpecifications.*;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID>, JpaSpecificationExecutor<AdminNotification> {

    /**
     * Count unread notifications
     */
    default long countUnread() {
        return count(isRead(false));
    }

    /**
     * Count notifications by type and time range
     */
    default long countByTypeAndTimeRange(AdminNotificationType type, OffsetDateTime from, OffsetDateTime to) {
        return count(hasType(type).and(createdBetween(from, to)));
    }

    /**
     * Count notifications by severity and time range
     */
    default long countBySeverityAndTimeRange(NotificationSeverity severity, OffsetDateTime from, OffsetDateTime to) {
        return count(hasSeverity(severity).and(createdBetween(from, to)));
    }

    /**
     * Count unread notifications created before a specific date
     */
    default long countUnreadAtDate(OffsetDateTime date) {
        return count(isRead(false).and(createdBefore(date)));
    }

    /**
     * Get notification counts per day, grouped by created_at date
     */
    @Query("SELECT CAST(n.createdAt AS LocalDate) as date, COUNT(n) as count FROM AdminNotification n " +
           "WHERE n.createdAt >= :startDate " +
           "GROUP BY CAST(n.createdAt AS LocalDate) " +
           "ORDER BY CAST(n.createdAt AS LocalDate)")
    List<Object[]> countNotificationsPerDay(@Param("startDate") OffsetDateTime startDate);
}
