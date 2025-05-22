package org.phong.horizon.admin.notification.infrastructure.repositories;

import org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, UUID>, JpaSpecificationExecutor<AdminNotification> {
}

