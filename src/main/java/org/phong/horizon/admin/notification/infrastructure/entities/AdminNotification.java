package org.phong.horizon.admin.notification.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationRelatedType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "admin_notifications", indexes = {
        @Index(name = "idx_admin_notifications_type", columnList = "type"),
        @Index(name = "idx_admin_notifications_severity", columnList = "severity"),
        @Index(name = "idx_admin_notifications_created_at", columnList = "created_at")
})
public class AdminNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false)
    private AdminNotificationType type;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "severity", nullable = false)
    private NotificationSeverity severity;

    @Column(name = "source", columnDefinition = "TEXT")
    private String source;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "related_type")
    private NotificationRelatedType relatedType;

    @Column(name = "related_id")
    private UUID relatedId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;
}

