package org.phong.horizon.historyactivity.infrstructure.persistence.enitities;

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
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "history_activity", indexes = {
        @Index(name = "idx_history_activity_type_created", columnList = "activity_type, created_at"),
        @Index(name = "idx_history_activity_target_type_user", columnList = "target_id, target_type, user_id, created_at"),
        @Index(name = "idx_history_activity_target_type", columnList = "target_id, target_type, created_at"),
        @Index(name = "idx_history_activity_user_type", columnList = "user_id, activity_type, created_at"),
        @Index(name = "idx_history_activity_created_at", columnList = "created_at")
})
public class HistoryActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "activity_description", nullable = false, length = Integer.MAX_VALUE)
    private String activityDescription;

    @Column(name = "activity_detail")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> activityDetail;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

}