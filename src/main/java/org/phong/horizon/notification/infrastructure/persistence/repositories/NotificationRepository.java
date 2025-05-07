package org.phong.horizon.notification.infrastructure.persistence.repositories;

import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.infrastructure.persistence.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isDeleted = true AND n.deletedAt IS NOT NULL AND n.deletedAt < :thresholdDate")
    int deleteOldSoftDeletedNotifications(@Param("thresholdDate") OffsetDateTime thresholdDate);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.recipientUser.id = :currentUserId")
    void markAllNotificationsAsRead(UUID currentUserId, Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.recipientUser.id = :currentUserId AND n.type = :notificationType")
    void markAllNotificationsAsReadByType(UUID currentUserId, NotificationType notificationType, Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = CURRENT_TIMESTAMP WHERE n.recipientUser.id = :currentUserId")
    void dismissAll(UUID currentUserId, boolean b);

    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = CURRENT_TIMESTAMP WHERE n.recipientUser.id = :currentUserId AND n.type = :notificationType")
    void dismissAllByType(UUID currentUserId, NotificationType notificationType, boolean b);
}