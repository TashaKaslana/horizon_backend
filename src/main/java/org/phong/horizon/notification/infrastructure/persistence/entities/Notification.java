package org.phong.horizon.notification.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.post.infraustructure.persistence.entities.Post;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_recipient_type", columnList = "recipient_user_id, type"),
        @Index(name = "idx_notifications_recipient_read_created", columnList = "recipient_user_id, is_read, created_at"),
        @Index(name = "idx_notifications_created_deleted", columnList = "created_at, is_deleted")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "sender_user_id")
    private User senderUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "content")
    private String content;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "notification_type not null")
    private NotificationType type;

    @ColumnDefault("'{}'")
    @Column(name = "extra_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> extraData;

    @ColumnDefault("false")
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @ColumnDefault("false")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}