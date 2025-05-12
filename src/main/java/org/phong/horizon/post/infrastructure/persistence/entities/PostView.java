package org.phong.horizon.post.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "post_views",
        indexes = {
                @Index(name = "idx_post_views_post_id", columnList = "post_id"),
                @Index(name = "idx_post_views_user_time", columnList = "user_id, post_id, viewed_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "ip_address")
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "viewed_at", nullable = false, updatable = false)
    private LocalDateTime viewedAt;
}
