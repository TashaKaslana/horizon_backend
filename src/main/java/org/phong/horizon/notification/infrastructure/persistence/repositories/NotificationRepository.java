package org.phong.horizon.notification.infrastructure.persistence.repositories;

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
}